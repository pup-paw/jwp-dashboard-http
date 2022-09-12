package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.http11.exception.MethodNotAllowedException;
import org.apache.coyote.http11.request.Http11Request;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11RequestTest {

    @DisplayName("허용된 메서드가 아니면 예외를 반환한다.")
    @Test
    void notAllowedMethod() {
        final String httpRequest = getHttp11RequestContent("ABC", "/index.html");
        final StubSocket socket = new StubSocket(httpRequest);

        assertThatThrownBy(() -> Http11Request.from(socket.getInputStream()))
                .isInstanceOf(MethodNotAllowedException.class);
    }

    public static String getHttp11RequestContent(String requestMethod, String requestUrl) {
        return String.join("\r\n",
                requestMethod + " " + requestUrl + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
    }
}
