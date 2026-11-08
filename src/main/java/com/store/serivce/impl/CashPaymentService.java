package com.store.serivce.impl;

import com.store.serivce.PaymentService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("cash")
public class CashPaymentService implements PaymentService {
    @Override
    public void processPayment() {
        // Implement cash payment processing logic here
        System.out.println("Processing payment via cash");
    }
}
