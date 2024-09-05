package com.sparta.preonboardingbackendcourse.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.preonboardingbackendcourse.domain.user.dto.request.LoginRequest;
import com.sparta.preonboardingbackendcourse.domain.user.dto.request.RefreshTokenRequest;
import com.sparta.preonboardingbackendcourse.domain.user.dto.request.SignupRequest;
import com.sparta.preonboardingbackendcourse.domain.user.dto.response.LoginResponse;
import com.sparta.preonboardingbackendcourse.domain.user.dto.response.SignupResponse;
import com.sparta.preonboardingbackendcourse.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class) // AuthController대상으로 웹 계층 테스트
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    //HTTP Mocking
    @Autowired
    private MockMvc mockMvc;

    // UserService을 Mocking
    @MockBean
    private UserService userService;

    // JSON 변환을 위해 ObjectMapper를 주입
    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("회원가입 성공 테스트")
    @Test
    @WithMockUser
    void signup() throws Exception {
        // given:
        SignupRequest request = new SignupRequest(
                "nickname",
                "password",
                "username"
        );

        // ObjectMapper를 사용해 Java 객체를 JSON 문자열로 변환
        String jsonRequest = objectMapper.writeValueAsString(request);

        //  userService.signup 호출 시 SignupResponse 반환
        SignupResponse response = SignupResponse.builder()
                .nickname("nickname")
                .username("username")
                .authorities(List.of("USER"))
                .build();

        // response: Service.signup()를 Mocking
        when(userService.signup(any(SignupRequest.class))).thenReturn(response);

        // when: HTTP POST REQUEST  then:
        mockMvc.perform(post("/api/auth/signup")
                        // 요청 본문이 JSON 형식임을 명시하고 본문에 넣기

                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                // HTTP 201 응답을 기대
                .andExpect(status().isCreated())
                .andExpect(jsonPath("data.nickname").value("nickname"))
                .andExpect(jsonPath("data.username").value("username"));
    }

    @DisplayName("로그인 성공 테스트")
    @Test
    //@WithMockUser(username = "user", roles = "USER")
    void login() throws Exception {
        // given:
        LoginRequest request = new LoginRequest(
                "username",
                "password"
        );
        LoginResponse response = new LoginResponse("accessToken", "refreshToken");
        String jsonRequest = objectMapper.writeValueAsString(request);

        when(userService.login(any(LoginRequest.class))).thenReturn(response);

        // when$then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", "accessToken"))
                .andExpect(header().string("Refresh-Token", "refreshToken"));
    }

    @DisplayName("리프레시 토큰을 이용한 엑세스 토큰 재발급 성공 테스트")
    @Test
    void refresh() throws Exception {
        // given:
        RefreshTokenRequest request = new RefreshTokenRequest("refreshToken");

        // 새로 발급될 AccessToken
        String newAccessToken = "newAccessToken";

        String jsonRequest = objectMapper.writeValueAsString(request);
        when(userService.refreshAccessToken(any(String.class))).thenReturn(newAccessToken);

        // when then:
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", newAccessToken));
    }
}