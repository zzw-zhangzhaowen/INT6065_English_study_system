package com.english.learning.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig
implements WebMvcConfigurer {
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(new String[]{"/uploads/**"}).addResourceLocations(new String[]{"file:./uploads/"});
        registry.addResourceHandler(new String[]{"/api/uploads/**"}).addResourceLocations(new String[]{"file:./uploads/"});
        registry.addResourceHandler(new String[]{"/photo/**"}).addResourceLocations(new String[]{"classpath:/photo/"});
        registry.addResourceHandler(new String[]{"/api/photo/**"}).addResourceLocations(new String[]{"classpath:/photo/"});
        registry.addResourceHandler(new String[]{"/covers/**"}).addResourceLocations(new String[]{"file:./resources/covers/"});
        registry.addResourceHandler(new String[]{"/api/covers/**"}).addResourceLocations(new String[]{"file:./resources/covers/"});
        registry.addResourceHandler(new String[]{"/resources/**"}).addResourceLocations(new String[]{"file:./resources/"});
        registry.addResourceHandler(new String[]{"/api/resources/**"}).addResourceLocations(new String[]{"file:./resources/"});
    }
}

