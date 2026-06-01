package com.example.provider;

import com.example.model.Offer;

import java.util.ArrayList;
import java.util.List;

public final class WalmartLikeClient extends SimulatedProviderClient {

    /**
     * Walmart client with custom timeout range.
     * @param minSleepMs minimum sleep time in milliseconds
     * @param maxSleepMs maximum sleep time in milliseconds
     */
    public WalmartLikeClient(int minSleepMs, int maxSleepMs) {
        super(minSleepMs, maxSleepMs);
    }

    /**
     * Walmart client with default timeout range (100-1200ms).
     */
    public WalmartLikeClient() {
        super();
    }

    @Override
    public String providerId() {
        return "walmart";
    }

    @Override
    protected List<Offer> generateOffers(String productId) {
        List<Offer> offers = new ArrayList<>();
        int count = 1 + random().nextInt(2);
        for (int i = 0; i < count; i++) {
            offers.add(new Offer(providerId(), 40 + random().nextInt(100), 1 + random().nextInt(4)));
        }
        return offers;
    }
}

