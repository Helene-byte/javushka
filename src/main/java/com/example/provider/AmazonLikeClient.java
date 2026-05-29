package com.example.provider;
import com.example.model.Offer;

import java.util.ArrayList;
import java.util.List;
public final class AmazonLikeClient extends SimulatedProviderClient {
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
