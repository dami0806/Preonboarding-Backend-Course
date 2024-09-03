package com.sparta.preonboardingbackendcourse.domain.user.service;

import com.sparta.preonboardingbackendcourse.domain.user.dto.LoginRequest;
import com.sparta.preonboardingbackendcourse.domain.user.dto.LoginResponse;
import com.sparta.preonboardingbackendcourse.domain.user.dto.SignupRequest;
import com.sparta.preonboardingbackendcourse.domain.user.dto.SignupResponse;
import com.sparta.preonboardingbackendcourse.domain.user.entity.User;
import com.sparta.preonboardingbackendcourse.domain.user.entity.UserStatus;
import com.sparta.preonboardingbackendcourse.domain.user.repository.UserRepository;

import com.sparta.preonboardingbackendcourse.domain.user.util.JwtUtil;
import com.sparta.preonboardingbackendcourse.global.exception.CustomException;
import com.sparta.preonboardingbackendcourse.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleService userRoleService;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public SignupResponse signup(SignupRequest signupRequest) {

        // 이미 존재하는 사용자 확인
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }
        // 비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());
        User user = User.builder()
                .password(encodedPassword)
                .username(signupRequest.getUsername())
                .userNickname(signupRequest.getNickname())
                .userStatus(UserStatus.PENDING) // 사용자 상태 비활성화 -> 로그인하면 그때 활성화시킬것
                .build();

        userRepository.save(user);

        // 권한 지정
        userRoleService.addUserRole(user,"USER"); // 지금은 임시로 USER로 권한 들어가게

        // 권한 정보 조회
        List<String> authorities = userRoleService.getUserRoles(user);

        return SignupResponse.builder()
                .username(user.getUsername())
                .nickname(user.getUserNickname())
                .authorities(authorities)
                .build();
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User user = getUserByName(loginRequest.getUsername());

        // 입력된 비밀번호와 저장된 해시된 비밀번호를 비교
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_FAIL);
        }

        // 유효한 사용자일때 토큰 발급
        String accessToken = JwtUtil.createAccessToken(user.getUsername(), user.getRoles());
        String refreshToken = JwtUtil.createRefreshToken(user.getUsername());

        // 리프레시 토큰을 Redis에 저장
        redisTemplate.opsForValue().set(user.getUsername(), refreshToken, jwtUtil.getRefreshTokenExpiration(), TimeUnit.MILLISECONDS);

        return new LoginResponse(accessToken, refreshToken);
    }

    public User getUserByName(String username) {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
