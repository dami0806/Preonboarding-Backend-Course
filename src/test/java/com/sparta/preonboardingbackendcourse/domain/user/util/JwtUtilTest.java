package com.sparta.preonboardingbackendcourse.domain.user.util;

import com.sparta.preonboardingbackendcourse.domain.user.config.JwtConfig;
import com.sparta.preonboardingbackendcourse.domain.user.entity.Role;
import com.sparta.preonboardingbackendcourse.domain.user.entity.UserRole;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class JwtUtilTest {
    //
    private final String secretKey = "e36f112d-c6f2-466f-aad8-14dcdc16360b";
    private long accessTokenExpiration = 1000; // 10초
    private long refreshTokenExpiration = 3000; // 30초

    @BeforeEach
    void setUp() {
        JwtUtil.init(new JwtConfig() {
            @Override
            public String getSecretKey() {
                return secretKey;
            }

            @Override
            public long getAccessTokenExpiration() {
                return accessTokenExpiration;
            }

            @Override
            public long getRefreshTokenExpiration() {
                return refreshTokenExpiration;
            }
        });
    }

    @DisplayName("JWT 엑세스 토큰 생성 및 검증")
    @Test
    void testCreateAndValidateAccessToken() {
        // given:
        String username = "testUser";
        Set<UserRole> roles = Set.of(UserRole.builder().role(new Role("USER")).build());

        // when: 엑세스 토큰 생성
        String token = JwtUtil.createAccessToken(username, roles);

        // then:
        assertNotNull(token);
        assertEquals(username, JwtUtil.getUsernameFromToken(token));
    }

    @DisplayName("JWT 리프레시 토큰 생성 및 검증")
    @Test
    void testCreateAndValidateRefreshToken() {
        // given:
        String username = "testUser";

        // when:
        String token = JwtUtil.createRefreshToken(username);

        // then:
        assertNotNull(token);
        assertEquals(username, JwtUtil.getUsernameFromToken(token));
    }

    @DisplayName("토큰 만료 체크")
    @Test
    void testIsTokenExpired() throws InterruptedException {
        // given:
        String username = "testUser";
        Set<UserRole> roles = Set.of(UserRole.builder().role(new Role("USER")).build());

        // when:
        String accessToken = JwtUtil.generateToken(username, 1000L, roles);

        // 1.1초 후에 토큰이 만료되도록 대기
        Thread.sleep(1100L);

        // then: 토큰이 만료되었는지 확인
        assertThrows(ExpiredJwtException.class, () -> {
            JwtUtil.validateToken(accessToken, mock(UserDetails.class));
        });
    }

    //토큰에서 사용자 추출
    @DisplayName("JWT에서 사용자 이름을 추출한다")
    @Test
    void testGetUsernameFromToken() {
        // given:
        Set<UserRole> roles = Set.of(UserRole.builder().role(new Role("USER")).build());

        // when: JWT 엑세스 토큰 생성
        String accessToken = JwtUtil.createAccessToken("testUser", roles);
        JwtUtil.getUsernameFromToken(accessToken);

        // then:
        assertEquals("testUser", JwtUtil.getUsernameFromToken(accessToken));
    }

    @DisplayName("JWT 토큰에서 역할 가져오기")
    @Test
    void testGetRolesFromToken() {
        // given:
        Set<UserRole> roles = Set.of(UserRole.builder().role(new Role("USER")).build());

        // when: JWT 엑세스 토큰 생성
        String accessToken = JwtUtil.createAccessToken("testUser", roles);

        // then: 역할 확인
        List<SimpleGrantedAuthority> authorities = JwtUtil.getRolesFromToken(accessToken);
        assertEquals(1, authorities.size());
        assertEquals("USER", authorities.get(0).getAuthority());
    }
}