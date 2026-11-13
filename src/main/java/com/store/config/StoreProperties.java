package com.store.config;

/*
 * CONFIG BEAN vs @Value:
 * Instead of scattering @Value("${store.name}") and @Value("${store.maxOrdersPerDay}")
 * across multiple service classes, we centralise all related config in one plain object.
 * Benefits: a single injection point, compile-time visibility of what config exists,
 * easy to construct in tests without a Spring context, and no @Value typos at runtime.
 */
public class StoreProperties {
    private final String storeName;
    private final int maxOrdersPerDay;

    public StoreProperties(String storeName, int maxOrdersPerDay) {
        this.storeName = storeName;
        this.maxOrdersPerDay = maxOrdersPerDay;
    }

    public String getStoreName() { return storeName; }
    public int getMaxOrdersPerDay() { return maxOrdersPerDay; }
}
