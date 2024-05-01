package com.yangyoung.english;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class EnglishApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnglishApplication.class, args);
    }

}
