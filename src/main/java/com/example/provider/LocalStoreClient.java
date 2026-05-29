package com.example.provider;

import com.example.model.Offer;

import java.util.ArrayList;
import java.util.List;

public final class LocalStoreClient extends SimulatedProviderClient {

    @Override
    public String providerId() {
        return "local-store";
    }

    @Override
    protected List<Offer> generateOffers(String productId) {
        List<Offer> offers = new ArrayList<>();
        int count = 1 + random().nextInt(3);
        for (int i = 0; i < count; i++) {
            offers.add(new Offer(providerId(), 30 + random().nextInt(150), 1 + random().nextInt(3)));
        }
        return offers;
    }
}

