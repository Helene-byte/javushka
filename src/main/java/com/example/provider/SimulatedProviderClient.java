package com.example.provider;

import com.example.model.Offer;

import java.util.List;
import java.util.Random;

/**
 * Base class for simulated providers.
 * Handles random delays and random failure injection.
 */
abstract class SimulatedProviderClient implements ProviderClient {

    private static final int MIN_SLEEP_MS = 100;
    private static final int MAX_SLEEP_MS = 1200;
    private static final double FAILURE_PROBABILITY = 0.20;

    private final Random random = new Random();

    /**
     * Simulates network latency, then returns offers or throws.
     */
    @Override
    public final List<Offer> fetchOffers(String productId) throws Exception {
        int sleepMs = MIN_SLEEP_MS + random.nextInt(MAX_SLEEP_MS - MIN_SLEEP_MS);
        Thread.sleep(sleepMs);

        if (random.nextDouble() < FAILURE_PROBABILITY) {
            throw new RuntimeException(providerId() + " failed to fetch offers for: " + productId);
        }

        return generateOffers(productId);
    }

    /**
     * Subclasses provide their own offer generation logic.
     */
    protected abstract List<Offer> generateOffers(String productId);

    protected Random random() {
        return random;
    }
}

