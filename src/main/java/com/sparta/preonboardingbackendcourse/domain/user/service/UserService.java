package com.sparta.preonboardingbackendcourse.domain.user.service;

import com.sparta.preonboardingbackendcourse.domain.user.dto.SignupRequest;
import com.sparta.preonboardingbackendcourse.domain.user.dto.SignupResponse;

public interface UserService {
    // 회원가입
    SignupResponse signup(SignupRequest signupRequest);
}
