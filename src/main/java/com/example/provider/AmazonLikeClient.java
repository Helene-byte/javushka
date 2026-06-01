package com.example.provider;
import com.example.model.Offer;

import java.util.ArrayList;
import java.util.List;
public final class AmazonLikeClient extends SimulatedProviderClient {

    /**
     * Amazon client with custom timeout range.
     * @param minSleepMs minimum sleep time in milliseconds
     * @param maxSleepMs maximum sleep time in milliseconds
     */
    public AmazonLikeClient(int minSleepMs, int maxSleepMs) {
        super(minSleepMs, maxSleepMs);
    }

    /**
     * Amazon client with default timeout range (100-1200ms).
     */
    public AmazonLikeClient() {
        super();
    }

    @Override
    public String providerId() {
        return "amazon";
    }
    @Override
    protected List<Offer> generateOffers(String productId) {
        List<Offer> offers = new ArrayList<>();
        int count = 1 + random().nextInt(3);
        for (int i = 0; i < count; i++) {
            offers.add(new Offer(providerId(), 50 + random().nextInt(200), 1 + random().nextInt(5)));
        }
        return offers;
    }
}
