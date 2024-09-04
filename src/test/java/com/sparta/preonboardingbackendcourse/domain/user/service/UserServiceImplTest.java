package com.sparta.preonboardingbackendcourse.domain.user.service;

import com.sparta.preonboardingbackendcourse.domain.user.config.JwtConfig;
import com.sparta.preonboardingbackendcourse.domain.user.dto.request.LoginRequest;
import com.sparta.preonboardingbackendcourse.domain.user.dto.request.SignupRequest;
import com.sparta.preonboardingbackendcourse.domain.user.dto.response.LoginResponse;
import com.sparta.preonboardingbackendcourse.domain.user.dto.response.SignupResponse;
import com.sparta.preonboardingbackendcourse.domain.user.entity.User;
import com.sparta.preonboardingbackendcourse.domain.user.entity.UserStatus;
import com.sparta.preonboardingbackendcourse.domain.user.repository.UserRepository;
import com.sparta.preonboardingbackendcourse.domain.user.util.JwtUtil;
import com.sparta.preonboardingbackendcourse.global.exception.CustomException;
import com.sparta.preonboardingbackendcourse.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private JwtConfig jwtConfig;
    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;  // Redis 값 처리 Mock

    @Mock
    private UserRoleService userRoleService;

    @InjectMocks
    private UserServiceImpl userService;


    // Mockito 초기화 Mock 주입 초기화
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // JWT 비밀키 설정
        given(jwtConfig.getSecretKey()).willReturn("e36f112d-c6f2-466f-aad8-14dcdc16360b");
        given(jwtConfig.getAccessTokenExpiration()).willReturn(1000L);  // 10초
        given(jwtConfig.getRefreshTokenExpiration()).willReturn(2000L); // 20초

        // RedisTemplate의 opsForValue()가 valueOperations를 반환하도록 Mocking
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        // JWT 유틸 초기화
        JwtUtil.init(jwtConfig);
    }

    @DisplayName("회원가입 성공")
    @Test
    void signupSuccess() {

        // given: 저장소에 중복된 사람이 없고, 비밀번호 암호화 상태
        SignupRequest request = new SignupRequest(
                "nickname",
                "password",
                "username");
        // 중복된 사용자 없음
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");


        // when: 회원가입 설정
        SignupResponse response = userService.signup(request);

        // then: 유저가 저장, 응답 확인
        verify(userRepository, times(1)).save(any(User.class));
        then(userRepository).should().save(any(User.class));

        assertEquals("nickname", response.getNickname());
        assertEquals("username", response.getUsername());
    }

    @DisplayName("회원가입 실패 - 중복된 회원")
    @Test
    void signupFailureDuplicateUser() {
        // given: 저장소에 중복된 사람이 없고, 비밀번호 암호화 상태
        SignupRequest request = new SignupRequest(
                "nickname",
                "password",
                "username");

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

        // when&then : 예외 발생하는지
        CustomException exception = assertThrows(CustomException.class, ()
                -> userService.signup(request));

        //then: 예외처리되는지, 저장 안되는지 확인
        assertEquals(ErrorCode.DUPLICATE_NICKNAME, exception.getErrorCode());
        verify(userRepository, never()).save(any(User.class));
    }

    @DisplayName("로그인 성공")
    @Test
    void loginSuccess() {
        //given
        LoginRequest request = new LoginRequest("username", "password");

        User user = User.builder()
                .username("username")
                .password("encodedPassword")
                .build();

        // 유저 찾기
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));
        // 비밀번호 확인하기
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);

        // jwt 발급
        // Mocking static
        try (MockedStatic<JwtUtil> mockedJwtUtil = mockStatic(JwtUtil.class)) {
            // jwt 발급을 Mocking
            mockedJwtUtil.when(() -> JwtUtil.createAccessToken(user.getUsername(), user.getRoles()))
                    .thenReturn("accessToken");
            mockedJwtUtil.when(() -> JwtUtil.createRefreshToken(user.getUsername()))
                    .thenReturn("refreshToken");

            // Redis에 토큰 저장 Mocking
            doNothing().when(valueOperations).set(anyString(), anyString(), anyLong(), any());

            // when: 로그인 실행
            LoginResponse response = userService.login(request);

            //then: 로그인 성공시 상태변화와 발급 확인
            assertEquals(UserStatus.ACTIVE, user.getUserStatus());
            assertEquals("accessToken", response.getAccessToken());
            assertEquals("refreshToken", response.getRefreshToken());
        }

    }

    @DisplayName("로그인 실패 - 회원없음")
    @Test
    void loginFailure() {
        //given
        LoginRequest request = new LoginRequest("username", "password");
        // 유저가 존재 하지 않음
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, ()
                -> userService.login(request));

        // then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @DisplayName("리프레시 토큰 재발급 성공")
    @Test
    void refreshAccessTokenSuccess() {

        // given: 유효한 JWT 형식의 리프레시 토큰
        String refreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSIsImlhdCI6MTUxNjIzOTAyMn0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        String username = "username";
        User user = User.builder()
                .username(username)
                .build();

        // RedisTemplate의 opsForValue가 호출해서 valueOperations 반환해서 리프레시 가져오기
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(username)).thenReturn(refreshToken);

        try (MockedStatic<JwtUtil> mockedJwtUtil = mockStatic(JwtUtil.class)) {

            mockedJwtUtil.when(() -> JwtUtil.getUsernameFromToken(refreshToken))
                    .thenReturn(username);

            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

            // 새로운 액세스 토큰 발급되도록 미리 mock 설정
            mockedJwtUtil.when(() -> JwtUtil.createAccessToken(username, user.getRoles()))
                    .thenReturn("newAccessToken");

            // when: 리프레시 토큰으로 새로운 액세스 토큰 요청
            String newAccessToken = userService.refreshAccessToken(refreshToken);

            // then: 새로운 액세스 토큰이 정상적으로 발급되었는지 확인
            assertEquals("newAccessToken", newAccessToken);

            mockedJwtUtil.verify(() -> JwtUtil.getUsernameFromToken(refreshToken), times(1));
            verify(redisTemplate, times(1)).opsForValue();
            verify(valueOperations, times(1)).get(username);
            verify(userRepository, times(1)).findByUsername(username);
            mockedJwtUtil.verify(() -> JwtUtil.createAccessToken(username, user.getRoles()), times(1));
        }
    }

    @DisplayName("리프레시 토큰 재발급 실패 - 만료된 토큰")
    @Test
    void refreshAccessTokenFailExpiredToken() {
        // given: 만료된 리프레시 토큰
        String expiredRefreshToken = "expiredToken";
        String username = "username";

        // JwtUtil의 getUsernameFromToken에서 만료된 토큰이라고 예외를 던지도록 Mocking
        try (MockedStatic<JwtUtil> mockedJwtUtil = mockStatic(JwtUtil.class)) {
            //만료된 토큰에 대한 예외를 발생시키기
            mockedJwtUtil.when(() -> JwtUtil.getUsernameFromToken(expiredRefreshToken))
                    .thenThrow(new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED));

            // when & then: 예외가 발생하는지 확인
            CustomException exception = assertThrows(CustomException.class, () -> {
                userService.refreshAccessToken(expiredRefreshToken);
            });

            // then: 예외
            assertEquals(ErrorCode.REFRESH_TOKEN_NOT_VALIDATE, exception.getErrorCode());

            // 추가 검증: Redis나 DB에 접근하지 않아야 한다
            verify(redisTemplate, never()).opsForValue();
            verify(userRepository, never()).findByUsername(anyString());
            mockedJwtUtil.verify(() -> JwtUtil.getUsernameFromToken(expiredRefreshToken), times(1));
        }
    }
}