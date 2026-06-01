package com.example.provider;

import com.example.model.Offer;

import java.util.ArrayList;
import java.util.List;

public final class EbayLikeClient extends SimulatedProviderClient {

    /**
     * eBay client with custom timeout range.
     * @param minSleepMs minimum sleep time in milliseconds
     * @param maxSleepMs maximum sleep time in milliseconds
     */
    public EbayLikeClient(int minSleepMs, int maxSleepMs) {
        super(minSleepMs, maxSleepMs);
    }

    /**
     * eBay client with default timeout range (100-1200ms).
     */
    public EbayLikeClient() {
        super();
    }

    @Override
    public String providerId() {
        return "ebay";
    }

    @Override
    protected List<Offer> generateOffers(String productId) {
        List<Offer> offers = new ArrayList<>();
        int count = 1 + random().nextInt(3);
        for (int i = 0; i < count; i++) {
            offers.add(new Offer(providerId(), 20 + random().nextInt(300), 2 + random().nextInt(10)));
        }
        return offers;
    }
}

