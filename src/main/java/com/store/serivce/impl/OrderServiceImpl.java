package com.store.serivce.impl;

import com.store.config.StoreProperties;
import com.store.serivce.NotificationService;
import com.store.serivce.OrderService;
import com.store.serivce.PaymentService;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/*
 * SCOPING PROBLEM: prototype bean injected into a singleton
 * ──────────────────────────────────────────────────────────
 * OrderServiceImpl is singleton-scoped (Spring default). NotificationService
 * implementations are @Scope("prototype") — a new instance should be created
 * per injection point.
 *
 * THE PROBLEM: if NotificationService were injected as a plain field or via a
 * setter (@Autowired NotificationService notificationService), Spring injects
 * it exactly ONCE when the singleton is constructed. Every subsequent call to
 * processOrder() reuses that same stale instance. The prototype scope is silently
 * defeated — you get singleton behaviour from a bean declared as prototype.
 *
 * THE FIX: inject ObjectFactory<NotificationService> instead. The factory itself
 * is singleton-safe; calling factory.getObject() asks the Spring context for a
 * brand-new prototype bean each time. Watch the instance IDs in the output below
 * to confirm: a different number on every call means a fresh bean.
 *
 * ──────────────────────────────────────────────────────────
 * WHY CONSTRUCTOR INJECTION IS PREFERRED OVER FIELD INJECTION
 * ──────────────────────────────────────────────────────────
 * Field injection (@Autowired directly on a field) works, but has real costs:
 *
 *   1. Hidden dependencies — callers cannot see what the class requires without
 *      reading the full source. The constructor signature is the public contract.
 *   2. No immutability — fields can't be final, so a dependency can be replaced
 *      after construction (accidentally or in tests).
 *   3. Harder to test — to create the class outside Spring you must use reflection
 *      or a Spring test runner. With constructor injection you just call `new`.
 *   4. Circular dependency detection — Spring catches constructor-based cycles at
 *      startup with a clear error. Field-injected cycles may be silently proxied
 *      and only fail at runtime.
 *
 * Setter injection is acceptable for truly optional dependencies. All mandatory
 * dependencies (like the ones below) should be constructor-injected.
 */
@Service
public class OrderServiceImpl implements OrderService {

    private final PaymentService paymentService;
    private final ObjectFactory<NotificationService> notificationFactory;
    private final StoreProperties storeProperties;

    @Autowired
    public OrderServiceImpl(
            @Qualifier("card") PaymentService paymentService,
            ObjectFactory<NotificationService> notificationFactory,
            StoreProperties storeProperties) {
        this.paymentService = paymentService;
        this.notificationFactory = notificationFactory;
        this.storeProperties = storeProperties;
    }

    @Override
    public void processOrder(String orderId) {
        System.out.println("\n[" + storeProperties.getStoreName() + "] Processing order: " + orderId
                + " (max per day: " + storeProperties.getMaxOrdersPerDay() + ")");

        paymentService.processPayment();

        // Each call to getObject() triggers @PostConstruct on a NEW NotificationService instance.
        // The identity hash confirms a fresh prototype bean — not the same one reused.
        NotificationService notification = notificationFactory.getObject();
        System.out.println("NotificationService instance id: " + System.identityHashCode(notification));
        notification.sendNotification();
    }
}
