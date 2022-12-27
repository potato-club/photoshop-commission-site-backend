package com.community.site.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://easyphoto.site", "http://localhost:3000")
                .exposedHeaders("authorization", "refreshToken") // 'authorization', 'refreshToken' 헤더 값을 받아온다
                .allowedHeaders("*")
                .allowedMethods("*")
                .allowCredentials(true);
    }
}
