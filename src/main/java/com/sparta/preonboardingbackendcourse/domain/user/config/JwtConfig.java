package com.sparta.preonboardingbackendcourse.domain.user.config;


import com.sparta.preonboardingbackendcourse.domain.user.util.JwtUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    // 시크릿 키
    @Value("${jwt.secret.key}")
    private String secretKey;

    // JWT Access 만료시간
    @Value("${jwt.token.expiration}")
    private long accessTokenExpiration;

    //JWT Refresh 만료시간
    @Value("${jwt.refresh.token.expiration}")
    private long refreshTokenExpiration;

    public String getSecretKey() {
        return secretKey;
    }

    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    @PostConstruct
    public void init() {
        JwtUtil.init(this);
    }
}
