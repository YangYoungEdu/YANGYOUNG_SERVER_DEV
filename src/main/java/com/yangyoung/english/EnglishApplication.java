package com.yangyoung.english;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class EnglishApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnglishApplication.class, args);
    }

    @PostConstruct
    public void init() {
        // timezone 설정
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}
