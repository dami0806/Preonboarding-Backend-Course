package com.sparta.preonboardingbackendcourse.global.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SwaggerConfig : - open API정보를 가진 객체 생성해서 api 문서 생성
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                // api 기본 정보
                .info(new Info()
                        .title("Pre-onboarding Backend Course API")
                        .version("1.0")
                        .description("Pre-onboarding Backend Course의 API 문서입니다"))

                // 서버 정보 추가 - 로컬로
                .addServersItem(new Server().url("http://localhost:8080")
                        .description("Local server"));
    }
}

