package org.apache.coyote.http11.exception;

public class MethodNotAllowedException extends RuntimeException {

    public MethodNotAllowedException() {
        super("사용할 수 없는 메서드입니다");
    }
}
