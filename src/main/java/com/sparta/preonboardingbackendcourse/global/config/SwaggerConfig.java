package com.sparta.preonboardingbackendcourse.global.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SwaggerConfig : - open API정보를 가진 객체 생성해서 api 문서 생성
 */
@Configuration
public class SwaggerConfig {

    @Value("${swagger.servers.local}")
    private String localServer;

    @Value("${swagger.servers.ec2}")
    private String ec2Server;

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                // api 기본 정보
                .info(new Info()
                        .title("Pre-onboarding Backend Course API")
                        .version("1.0")
                        .description("Pre-onboarding Backend Course의 API 문서입니다"))


                // 서버 정보 추가 - 로컬로
                .addServersItem(new Server().url(localServer)
                        .description("Local server"))

                // 배포된 EC2 서버 정보 추가
                .addServersItem(new Server()
                        .url(ec2Server)
                        .description("EC2 server"));
    }
}

