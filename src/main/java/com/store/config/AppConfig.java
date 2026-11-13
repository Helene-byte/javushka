package com.store.config;

import com.store.serivce.PaymentService;
import com.store.serivce.impl.CardPaymentService;
import com.store.serivce.impl.CashPaymentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.store")
public class AppConfig {

    @Bean(name = "card")
    public PaymentService cardPaymentService() {
        return new CardPaymentService();
    }

    @Bean(name = "cash")
    public PaymentService cashPaymentService() {
        return new CashPaymentService();
    }

    // Config bean: all store settings in one place rather than @Value fields scattered across services.
    @Bean
    public StoreProperties storeProperties() {
        return new StoreProperties("Spring Demo Store", 50);
    }
}
