package com.example.yousong.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Erlaube alle Anfragen von http://localhost:8080 (deine Vue.js-App)
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080")  // Dein Frontend-URL
                .allowedMethods("GET", "POST", "PUT", "DELETE","OPTIONS")
                //.allowCredentials(true)  // Falls du Cookies oder Sessions verwendest
                .allowedHeaders("*");  // Erlaubt alle Header
    }
}
