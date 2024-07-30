package com.yangyoung.english.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CORSConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry corsRegistry) {

                //for develop
                corsRegistry
                        .addMapping("/**")
                        .allowedOriginPatterns("https://www.ebhttps.store/", "http://localhost:3000")
                        .allowedMethods("GET", "POST", "PATCH", "DELETE", "OPTION");
            }
        };
    }
}
