package com.github.shangtanlin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")   // 如果线上建议写具体域名
                .allowedMethods("*")
                .allowedHeaders("*")   // 必须允许自定义头
                .exposedHeaders("Authorization")
                .allowCredentials(false);
    }
}

