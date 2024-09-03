package com.sparta.preonboardingbackendcourse.global.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ResponseDto<T> {
    private String message;
    private T data;

    @Builder
    public ResponseDto(String message, T data) {
        this.message = message;
        this.data = data;
    }
}

