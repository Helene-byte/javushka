package com.store.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.store")
public class AppConfig {

    // Config bean: all store settings in one place rather than @Value fields scattered across services.
    @Bean
    public StoreProperties storeProperties() {
        return new StoreProperties("Spring Demo Store", 50);
    }
}
