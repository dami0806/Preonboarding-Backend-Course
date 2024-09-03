package com.sparta.preonboardingbackendcourse.global.exception;

import lombok.Getter;

/**
 * CustomException - 에러코드와 메시지 커스텀 exception
 */
@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
