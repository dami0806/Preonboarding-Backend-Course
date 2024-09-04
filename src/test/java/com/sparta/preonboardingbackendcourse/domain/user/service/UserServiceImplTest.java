package com.sparta.preonboardingbackendcourse.domain.user.service;

import com.sparta.preonboardingbackendcourse.domain.user.dto.request.SignupRequest;
import com.sparta.preonboardingbackendcourse.domain.user.dto.response.SignupResponse;
import com.sparta.preonboardingbackendcourse.domain.user.entity.User;
import com.sparta.preonboardingbackendcourse.domain.user.repository.UserRepository;
import com.sparta.preonboardingbackendcourse.domain.user.util.JwtUtil;
import com.sparta.preonboardingbackendcourse.global.exception.CustomException;
import com.sparta.preonboardingbackendcourse.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    private UserRoleService userRoleService;

    @InjectMocks
    private UserServiceImpl userService;


    // Mockito 초기화 Mock 주입 초기화
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("회원가입 성공")
    @Test
    void signupSuccess() {

        // given: 저장소에 중복된 사람이 없고, 비밀번호 암호화 상태
        SignupRequest signupRequest = new SignupRequest(
                "nickname",
                "password",
                "username");
        // 중복된 사용자 없음
        when(userRepository.existsByUsername(signupRequest.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn("encodedPassword");


        // when: 회원가입 설정
        SignupResponse response = userService.signup(signupRequest);

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
        SignupRequest signupRequest = new SignupRequest(
                "nickname",
                "password",
                "username");

        when(userRepository.existsByUsername(signupRequest.getUsername())).thenReturn(true);

        // when&then : 예외 발생하는지
        CustomException exception = assertThrows(CustomException.class, ()
                -> userService.signup(signupRequest));

        //then: 예외처리되는지, 저장 안되는지 확인
        assertEquals(ErrorCode.DUPLICATE_NICKNAME, exception.getErrorCode());
        verify(userRepository, never()).save(any(User.class));
    }

    @DisplayName("로그인 - 성공")
    @Test
    void loginSuccess() {}
}