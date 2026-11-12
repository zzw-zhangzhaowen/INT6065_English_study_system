package com.english.learning;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan(value={"com.english.learning.mapper"})
@EnableAsync
@EnableScheduling
public class EnglishLearningSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(EnglishLearningSystemApplication.class, args);
        System.out.println("\n====================================");
        System.out.println("\u82f1\u8bed\u7b49\u7ea7\u667a\u80fd\u5b66\u4e60\u4e0e\u8003\u8bd5\u7cfb\u7edf\u542f\u52a8\u6210\u529f\uff01");
        System.out.println("Swagger API\u6587\u6863: http://localhost:8080/swagger-ui.html");
        System.out.println("====================================\n");
    }
}

