package com.example.provider;

import com.example.model.Offer;

import java.util.ArrayList;
import java.util.List;

public final class ExpressDeliveryClient extends SimulatedProviderClient {

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

