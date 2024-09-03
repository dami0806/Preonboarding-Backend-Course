package com.sparta.preonboardingbackendcourse.domain.user.controller;

import com.sparta.preonboardingbackendcourse.domain.user.dto.*;
import com.sparta.preonboardingbackendcourse.domain.user.service.UserService;
import com.sparta.preonboardingbackendcourse.global.dto.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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
    public ResponseEntity<ResponseDto<SignupResponse>> signup(@Valid @RequestBody SignupRequest signupRequest) {
        SignupResponse signupResponse = userService.signup(signupRequest);
        ResponseDto<SignupResponse> responseDto = new ResponseDto<>(
                "회원가입 성공", signupResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse tokens = userService.login(loginRequest); // 로그인 시도 및 토큰 생성
        String accessToken = tokens.getAccessToken();
        String refreshToken = tokens.getRefreshToken();

        // 각 토큰을 별도의 헤더에 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", accessToken);
        headers.set("Refresh-Token", refreshToken);

        return new ResponseEntity<>("로그인 성공", headers, HttpStatus.OK);
    }

    // 리프레시 토큰을 이용한 엑세스 토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        String newAccessToken = userService.refreshAccessToken(refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", newAccessToken);
        return new ResponseEntity<>("엑세스 토큰 재발급 성공", headers, HttpStatus.OK);
    }
}
