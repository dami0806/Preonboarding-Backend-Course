package com.sparta.preonboardingbackendcourse.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class SignupResponse {

    private final String username;
    private final String nickname;
    private final List<String> authorities;

    @Builder
    public SignupResponse(String username, String nickname, List<String> authorities) {
        this.username = username;
        this.nickname = nickname;
        this.authorities = authorities;
    }
}
