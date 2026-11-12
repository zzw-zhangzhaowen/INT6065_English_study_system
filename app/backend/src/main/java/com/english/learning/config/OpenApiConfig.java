package com.english.learning.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info().title("\u82f1\u8bed\u7b49\u7ea7\u667a\u80fd\u5b66\u4e60\u4e0e\u8003\u8bd5\u7cfb\u7edf API\u6587\u6863").version("1.0.0").description("\u63d0\u4f9b\u5b8c\u6574\u7684RESTful API\u63a5\u53e3\u6587\u6863").contact(new Contact().name("\u7cfb\u7edf\u7ba1\u7406\u5458").email("admin@example.com")));
    }
}

