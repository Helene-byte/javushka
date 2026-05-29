package com.example.aggregator;

import com.example.model.Offer;
import com.example.provider.ProviderClient;

import java.util.List;

public final class TestProviders {
    private TestProviders() {
    }

    public static ProviderClient fixedProvider(String id, Offer... offers)
    {
        return new ProviderClient() {
            @Override
            public String providerId() {
                return id;
            }

            @Override
            public List<Offer> fetchOffers(String productId) {
                return List.of(offers);
            }
        };
    }
    public static ProviderClient failingProvider(String id) {
        return new ProviderClient() {
            @Override
            public String providerId() {
                return id;
            }

            @Override
            public List<Offer> fetchOffers(String productId) {
                throw new RuntimeException("failing provider");
            }
        };
    }

    public static ProviderClient slowProvider(String id, long sleepMs) {
        return new ProviderClient() {
            @Override
            public String providerId() {
                return id;
            }
            @Override
            public List<Offer> fetchOffers(String productId) throws Exception{
                Thread.sleep(sleepMs);
                return List.of(new Offer(id, 1, 1));
            }
        };
    }
}
