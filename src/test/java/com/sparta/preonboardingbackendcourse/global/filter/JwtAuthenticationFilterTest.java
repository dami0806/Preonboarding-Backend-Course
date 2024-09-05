package com.sparta.preonboardingbackendcourse.global.filter;

import com.sparta.preonboardingbackendcourse.domain.user.util.JwtUtil;
import com.sparta.preonboardingbackendcourse.global.exception.CustomException;
import com.sparta.preonboardingbackendcourse.global.exception.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private final String secretKey = "e36f112d-c6f2-466f-aad8-14dcdc16360b";

    @BeforeEach
    void setUp() {

    }

    @DisplayName("JWT 토큰이 유효하면 인증 정보를 설정한다.")
    @Test
    void testDoFilterInternalValidToken() throws Exception {

    }

    @DisplayName("JWT 토큰이 만료되면 예외를 발생시킨다.")
    @Test
    void testDoFilterInternalExpiredToken() throws Exception {

    }
}
