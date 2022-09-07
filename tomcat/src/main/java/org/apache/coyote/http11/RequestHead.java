package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHead {

    private static final Logger log = LoggerFactory.getLogger(RequestHead.class);
    private static final int REQUEST_HEADER_KEY_INDEX = 0;
    private static final int REQUEST_HEADER_VALUE_INDEX = 1;

    private final Map<String, String> headers;

    public RequestHead(Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHead of(final BufferedReader bufferedReader) throws IOException {
        Map<String, String> requestHeaders = new HashMap<>();
        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            String[] keyValue = line.split(":");
            requestHeaders.put(keyValue[REQUEST_HEADER_KEY_INDEX].trim(), keyValue[REQUEST_HEADER_VALUE_INDEX].trim());
            line = bufferedReader.readLine();
        }
        return new RequestHead(requestHeaders);
    }

    public int getContentLength() {
        String contentLength = headers.get("Content-Length");
        if (contentLength == null) {
            return 0;
        }
        return Integer.parseInt(contentLength);
    }

    public boolean hasNoCookieNamed(String cookieName) {
        String cookieHeaderContent = headers.get("Cookie");
        if (cookieHeaderContent == null) {
            return true;
        }
        HttpCookie cookieHeader = HttpCookie.from(cookieHeaderContent);
        return cookieHeader.hasNoNamed(cookieName);
    }
}
