package com.sparta.preonboardingbackendcourse.domain.user.service;

import com.sparta.preonboardingbackendcourse.domain.user.dto.request.LoginRequest;
import com.sparta.preonboardingbackendcourse.domain.user.dto.response.LoginResponse;
import com.sparta.preonboardingbackendcourse.domain.user.dto.request.SignupRequest;
import com.sparta.preonboardingbackendcourse.domain.user.dto.response.SignupResponse;

public interface UserService {
    // 회원가입
    SignupResponse signup(SignupRequest signupRequest);

    // 로그인
    LoginResponse login(LoginRequest loginRequest);

    // 토큰 재발급
    String refreshAccessToken(String refreshToken);

}
