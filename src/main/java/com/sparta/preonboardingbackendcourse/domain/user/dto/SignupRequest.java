package com.sparta.preonboardingbackendcourse.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignupRequest {
    @NotBlank(message = "닉네임은 필수 입력 값 입니다.")
    private String nickname;

    @NotBlank(message = "비밀번호는 필수 입력 값 입니다.")
    private String password;

    @NotBlank(message = "이름은 필수 입력 값 입니다.")
    private String username;
}
