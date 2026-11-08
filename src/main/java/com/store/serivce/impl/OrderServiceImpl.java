package com.store.serivce.impl;

import com.store.serivce.NotificationService;
import com.store.serivce.OrderService;
import com.store.serivce.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {
    private final PaymentService paymentService;
    private NotificationService notificationService;

    @Autowired
    public OrderServiceImpl(@Qualifier("card") PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Autowired
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void processOrder(String orderId) {
        // Implement the logic to process the order with the given orderId
        System.out.println("Order received : " + orderId);
        paymentService.processPayment();
        notificationService.sendNotification();
    }
}
