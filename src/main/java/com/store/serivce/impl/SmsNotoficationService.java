package com.store.serivce.impl;

import com.store.config.SmsCondition;
import com.store.serivce.NotificationService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Conditional(SmsCondition.class)
public class SmsNotoficationService implements NotificationService {
    @PostConstruct
    public void init() {
        System.out.println("SMS Notification Bean initialized.");
    }

    @Override
    public void sendNotification() {
        System.out.println("Sending SMS notification");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("SMS Notification Bean destroyed.");
    }
}
