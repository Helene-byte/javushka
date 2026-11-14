package com.store;

import com.store.config.AppConfig;
import com.store.serivce.OrderService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            OrderService orderService = context.getBean(OrderService.class);

            // The same singleton OrderService is used for all three calls.
            // Watch the NotificationService instance IDs — each call gets a fresh prototype bean,
            // proving that ObjectFactory correctly scopes the prototype inside the singleton.
            orderService.processOrder("order-1");
            orderService.processOrder("order-2");
            orderService.processOrder("order-3");
        }
    }
}
