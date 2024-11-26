package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // welche Pfade CORS erlauben sollen
                .allowedOrigins("http://localhost:8080") // URL von Vue.js Frontends
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Erlaubte HTTP-Methoden
                .allowedHeaders("*") // Erlaubt alle Header
                .allowCredentials(true); //  Cookies oder Authentifizierung
    }
}
