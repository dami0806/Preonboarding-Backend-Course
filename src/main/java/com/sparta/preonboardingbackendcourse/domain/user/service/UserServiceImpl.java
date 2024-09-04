package com.sparta.preonboardingbackendcourse.domain.user.service;

import com.sparta.preonboardingbackendcourse.domain.user.dto.request.LoginRequest;
import com.sparta.preonboardingbackendcourse.domain.user.dto.response.LoginResponse;
import com.sparta.preonboardingbackendcourse.domain.user.dto.request.SignupRequest;
import com.sparta.preonboardingbackendcourse.domain.user.dto.response.SignupResponse;
import com.sparta.preonboardingbackendcourse.domain.user.entity.User;
import com.sparta.preonboardingbackendcourse.domain.user.entity.UserStatus;
import com.sparta.preonboardingbackendcourse.domain.user.repository.UserRepository;

import com.sparta.preonboardingbackendcourse.domain.user.util.JwtUtil;
import com.sparta.preonboardingbackendcourse.global.exception.CustomException;
import com.sparta.preonboardingbackendcourse.global.exception.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleService userRoleService;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    @Transactional
    public SignupResponse signup(SignupRequest signupRequest) {

        // 이미 존재하는 사용자 확인
        validateDuplicateUser(signupRequest);

        User user = createUser(signupRequest);

        saveUser(user);

        // 권한 지정
        assignUserRole(user, "USER");

        // 권한 정보 조회
        List<String> authorities = userRoleService.getUserRoles(user);

        return buildSignupResponse(user, authorities);
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {

        // 사용자 자격 증명 - 사용자 찾고, 비밀번호 확인
        User user = validateUserCredentials(loginRequest.getUsername(), loginRequest.getPassword());

        // 유효한 사용자일때 토큰 발급
        String accessToken = JwtUtil.createAccessToken(user.getUsername(), user.getRoles());
        String refreshToken = JwtUtil.createRefreshToken(user.getUsername());

        user.login();
        saveUser(user);

        // 리프레시 토큰을 Redis에 저장
        storeRefreshTokenInRedis(user.getUsername(), refreshToken);
        log.info("새토큰 발급{}", user.getUsername());

        return new LoginResponse(accessToken, refreshToken);
    }

    @Override
    public String refreshAccessToken(String refreshToken) {

        String username = extractUsernameFromToken(refreshToken);

        validateRefreshToken(username, refreshToken);

        User user = getUserByName(username);

        return jwtUtil.createAccessToken(username, user.getRoles());
    }

    // 중복된 사용자인지 확인
    private void validateDuplicateUser(SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }
    }

    // 사용자 생성
    private User createUser(SignupRequest signupRequest) {
        // 비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        return User.builder()
                .password(encodedPassword)
                .username(signupRequest.getUsername())
                .userNickname(signupRequest.getNickname())
                .userStatus(UserStatus.PENDING) // 사용자 상태 비활성화 -> 로그인하면 그때 활성화시킬것
                .build();
    }

    // 사용자 저장하기
    private User saveUser(User user) {
        return userRepository.save(user);
    }

    private String extractUsernameFromToken(String token) {
        try {
            return jwtUtil.getUsernameFromToken(token);
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_VALIDATE);
        }
    }

    // 회원가입 응답
    private SignupResponse buildSignupResponse(User user, List<String> authorities) {
        return SignupResponse.builder()
                .username(user.getUsername())
                .nickname(user.getUserNickname())
                .authorities(authorities)
                .build();
    }

    // 사용자 권한 지정
    private void assignUserRole(User user, String role) {
        userRoleService.addUserRole(user, role); // 지금은 임시로 USER로 권한 들어가게
    }

    // 사용자 자격 증명 - 사용자 찾고, 비밀번호 확인
    private User validateUserCredentials(String username, String password) {
        User user = getUserByName(username);

        // 입력된 비밀번호와 저장된 해시된 비밀번호를 비교
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_FAIL);
        }
        return user;
    }

    // 레디스에 리프레시 토큰 저장
    private void storeRefreshTokenInRedis(String username, String refreshToken) {
        redisTemplate.opsForValue().set(username, refreshToken,
                jwtUtil.getRefreshTokenExpiration(),
                TimeUnit.MILLISECONDS);
    }

    // 리프레시토큰에 대한 사용자가 맞는지
    private void validateRefreshToken(String username, String refreshToken) {

        String storedRefreshToken = redisTemplate.opsForValue().get(username);

        // 저장된 리프레시 토큰과 요청의 리프레시 토큰이 일치하는지 확인
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_VALIDATE);
        }

        // 만료 코드 추가 -1이면 기한이 없고, -2는 만료
        Long expirationTime = redisTemplate.getExpire(username, TimeUnit.MILLISECONDS);
        if (expirationTime == null || expirationTime < 0) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED); // 만료된 토큰에 대한 에러 발생
        }
    }

    // 유저 찾기
    public User getUserByName(String username) {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
