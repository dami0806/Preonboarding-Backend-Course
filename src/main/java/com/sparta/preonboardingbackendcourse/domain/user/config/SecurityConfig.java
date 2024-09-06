package com.sparta.preonboardingbackendcourse.domain.user.config;

import com.sparta.preonboardingbackendcourse.global.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * SecurityConfig- Spring Security 설정
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * SecurityFilterChain: 보안 설정을 기반으로 SecurityFilterChain 생성
     *
     * @param http : HttpSecurity객체로, CSRF 비활성화, 세션 관리, 요청 권한, 필터 추가
     * @return SecurityFilterChain객체 생성(http 요청의 보안 규칙이 적용된)
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 설정
        http.csrf((csrf) -> csrf.disable())  //csrf 비활성화

                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 관리를 stateless설정

                // 요청에 대한 권한 설정
                .authorizeHttpRequests(authorizeHttpRequests ->
                                authorizeHttpRequests
                                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // resources 접근 허용 설정
                                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Swagger 접근 허용
                                        .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll() // 로그인, 회원가입 접근 허용
                                        .anyRequest().permitAll() // 그 외 모든 요청 접근 허용
                        //.anyRequest().authenticated() // 그 외 모든 요청 인증처리
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // jwtAuthenticationFilter의 순서를 지정해주기위해 UsernamePasswordAuthenticationFilter전으로 위치 지정
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    // CORS 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8080", "http://15.165.74.149:8080"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedMethods(List.of("*")); // 모든 HTTP 메서드 허용
        configuration.setAllowedHeaders(List.of("*")); // 모든 헤더 허용
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}