package com.sparta.preonboardingbackendcourse.domain.user.controller;

import com.sparta.preonboardingbackendcourse.domain.user.dto.SignupRequest;
import com.sparta.preonboardingbackendcourse.domain.user.dto.SignupResponse;
import com.sparta.preonboardingbackendcourse.domain.user.service.UserService;
import com.sparta.preonboardingbackendcourse.global.dto.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ResponseDto<SignupResponse>>signup(@Valid @RequestBody SignupRequest signupRequest) {
        SignupResponse signupResponse = userService.signup(signupRequest);
        ResponseDto<SignupResponse> responseDto = new ResponseDto<>(
                "회원가입 성공", signupResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 로그인

    // 엑세스토큰 재발급



}
