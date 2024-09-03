package com.sparta.preonboardingbackendcourse.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    // user 관련 오류 처리
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 사용중인 닉네임입니다."),
    DUPLICATE_USER_ID(HttpStatus.CONFLICT, "이미 사용중인 ID입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    LOGIN_FAIL(HttpStatus.UNAUTHORIZED, "로그인에 실패했습니다."),
    WRONG_HTTP_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 HTTP 요청입니다."),
    INCORRECT_PASSWORD(HttpStatus.BAD_REQUEST, "현재 비밀번호가 일치하지 않습니다."),
    REFRESH_TOKEN_NOT_VALIDATE(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었거나 잘못되었습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다.");
    private final HttpStatus status;
    private final String message;
}