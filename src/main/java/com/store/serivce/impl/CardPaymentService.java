package com.store.serivce.impl;

import com.store.serivce.PaymentService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("card")
public class CardPaymentService implements PaymentService {
    @Override
    public void processPayment() {
        // Implement card payment processing logic here
        System.out.println("Processing payment via card");
    }
}
