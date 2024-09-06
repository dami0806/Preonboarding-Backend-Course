package com.sparta.preonboardingbackendcourse;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@OpenAPIDefinition(servers = {@Server(url = "/", description = "Server URL")})
@SpringBootApplication
public class PreonboardingBackendCourseApplication {

    public static void main(String[] args) {
        SpringApplication.run(PreonboardingBackendCourseApplication.class, args);
    }

}
