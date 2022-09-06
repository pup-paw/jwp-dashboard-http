package nextstep.jwp;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11QueryParams;
import org.apache.coyote.http11.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Url {

    DEFAULT("/"::equals, url -> new Http11Response("200 OK", "html", "Hello world!")),
    LOGIN(url -> isMatchRegex("/login.*", url), url -> {
        if (isLoginSuccess(url)) {
            return Http11Response.from("302 FOUND", "/index.html");
        }
        return Http11Response.from("200 OK", "/login.html");
    }),
    RESOURCE(url -> isMatchRegex(".*\\..*", url), url -> Http11Response.from("200 OK", url));

    private static final Logger log = LoggerFactory.getLogger(Url.class);

    private final Predicate<String> condition;
    private final Function<String, Http11Response> resourcePathExtractor;

    Url(Predicate<String> condition, Function<String, Http11Response> resourcePathExtractor) {
        this.condition = condition;
        this.resourcePathExtractor = resourcePathExtractor;
    }

    public static Http11Response getResponseFrom(String url) {
        return Arrays.stream(values())
                .filter(value -> value.condition.test(url))
                .map(value -> value.resourcePathExtractor.apply(url))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("존재하지 않는 페이지입니다."));
    }

    private static boolean isMatchRegex(String regex, String url) {
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(url);
        return matcher.find();
    }

    private static boolean isLoginSuccess(String url) {
        final Http11QueryParams queryParams = Http11QueryParams.from(url);
        final String account = queryParams.getValueFrom("account");
        final String password = queryParams.getValueFrom("password");

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        if (user.checkPassword(password)) {
            log.info(user.toString());
            return true;
        }
        return false;
    }
}
