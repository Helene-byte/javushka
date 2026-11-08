package com.store;

import com.store.config.AppConfig;
import com.store.serivce.OrderService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
       try(AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
          OrderService orderService = context.getBean(OrderService.class);
          orderService.processOrder("order-1");
       }
    }
}