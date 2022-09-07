package org.apache.coyote.support;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.ui.DefaultController;
import nextstep.jwp.ui.IndexController;
import nextstep.jwp.ui.LoginController;
import nextstep.jwp.ui.RegisterController;
import nextstep.jwp.ui.ResourceController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ControllerMapper {

    DEFAULT("/"::equals, new DefaultController()),
    INDEX("/index.html"::equals, new IndexController()),
    LOGIN("/login"::equals, new LoginController()),
    REGISTER("/register"::equals, new RegisterController()),
    RESOURCE(ControllerMapper::isStaticResource, new ResourceController());

    private static final Logger log = LoggerFactory.getLogger(ControllerMapper.class);

    private final Predicate<String> condition;
    private final Controller controller;

    ControllerMapper(final Predicate<String> condition, final Controller controller) {
        this.condition = condition;
        this.controller = controller;
    }

    public static Controller getControllerFrom(final String requestUrl) {
        return Arrays.stream(values())
                .filter(value -> value.condition.test(requestUrl))
                .findAny()
                .map(value -> value.controller)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 API 입니다."));
    }

    private static boolean isStaticResource(final String url) {
        final Pattern pattern = Pattern.compile(".*\\..*");
        final Matcher matcher = pattern.matcher(url);
        return matcher.find();
    }
}
