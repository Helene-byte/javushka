package com.example.cdp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CdpApplication {

    public static void main(String[] args) {
        SpringApplication.run(CdpApplication.class, args);
    }
}

