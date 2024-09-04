package com.sparta.preonboardingbackendcourse.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequest {

    @NotBlank(message = "이름은 필수 입력 값 입니다.")
    private String username;      // 이름

    @NotBlank(message = "비밀번호는 필수 입력 값 입니다.")
    private String password;      // 비밀번호

}
