package com.example.provider;

import com.example.model.Offer;

import java.util.ArrayList;
import java.util.List;

public final class ExpressDeliveryClient extends SimulatedProviderClient {

    /**
     * Express delivery client with custom timeout range.
     * @param minSleepMs minimum sleep time in milliseconds
     * @param maxSleepMs maximum sleep time in milliseconds
     */
    public ExpressDeliveryClient(int minSleepMs, int maxSleepMs) {
        super(minSleepMs, maxSleepMs);
    }

    /**
     * Express delivery client with default timeout range (100-1200ms).
     */
    public ExpressDeliveryClient() {
        super();
    }

    @Override
    public String providerId() {
        return "express-delivery";
    }

    @Override
    protected List<Offer> generateOffers(String productId) {
        List<Offer> offers = new ArrayList<>();
        int count = 1 + random().nextInt(2);
        for (int i = 0; i < count; i++) {
            // Premium pricing but guaranteed next-day delivery
            offers.add(new Offer(providerId(), 150 + random().nextInt(200), 1));
        }
        return offers;
    }
}

