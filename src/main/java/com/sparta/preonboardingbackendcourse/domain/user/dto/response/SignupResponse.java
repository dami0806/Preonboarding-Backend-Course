package com.sparta.preonboardingbackendcourse.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class SignupResponse {

    private final String nickname;
    private final List<String> authorities;
    private final String username;

    @Builder
    public SignupResponse(String nickname, String username, List<String> authorities) {
        this.nickname = nickname;
        this.username = username;
        this.authorities = authorities;
    }
}
