package com.example.provider;

import com.example.model.Offer;

import java.util.ArrayList;
import java.util.List;

public final class WalmartLikeClient extends SimulatedProviderClient {

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

