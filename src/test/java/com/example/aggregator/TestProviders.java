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
            public List<Offer> fetchOffers(String productId) {
                try {
                    Thread.sleep(sleepMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
                return List.of(new Offer(id, 1, 1));
            }
        };
    }
}
