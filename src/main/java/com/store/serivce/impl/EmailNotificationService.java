package com.store.serivce.impl;

import com.store.config.EmailCondition;
import com.store.serivce.NotificationService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Conditional(EmailCondition.class)
public class EmailNotificationService implements NotificationService {
    @PostConstruct
    public void init() {
        System.out.println("Email Notification Bean initialized.");
    }

    @Override
    public void sendNotification() {
        System.out.println("Sending email notification...");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("Bean destroyed.");
    }
}
