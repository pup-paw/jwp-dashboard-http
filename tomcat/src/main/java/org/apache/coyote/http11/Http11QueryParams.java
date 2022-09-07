package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Http11QueryParams {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final Pattern QUERY_PARAM_PATTERN = Pattern.compile(".+=.+");

    private final Map<String, String> queryParams;

    private Http11QueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public static Http11QueryParams from(final String url) {
        final int index = url.indexOf("?");
        final String[] keyValues = url.substring(index + 1).split("&");
        return new Http11QueryParams(extractQueryParams(keyValues));
    }

    private static Map<String, String> extractQueryParams(final String[] keyValues) {
        return Arrays.stream(keyValues)
                .filter(keyValue -> QUERY_PARAM_PATTERN.matcher(keyValue).find())
                .map(keyValue -> keyValue.split("="))
                .collect(Collectors.toMap(keyValue -> keyValue[KEY_INDEX], keyValue -> keyValue[VALUE_INDEX]));
    }

    public boolean hasQueryParams(final List<String> keys) {
        return keys.stream()
                .allMatch(key -> getValueFrom(key) != null);
    }

    public String getValueFrom(final String key) {
        return queryParams.get(key);
    }
}
