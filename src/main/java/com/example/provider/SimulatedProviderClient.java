package com.example.provider;

import com.example.model.Offer;

import java.util.List;
import java.util.Random;

/**
 * Base class for simulated providers.
 * Handles random delays and random failure injection.
 */
abstract class SimulatedProviderClient implements ProviderClient {

    private static final double FAILURE_PROBABILITY = 0.20;
    private static final int DEFAULT_MIN_SLEEP_MS = 100;
    private static final int DEFAULT_MAX_SLEEP_MS = 1200;

    private final int minSleepMs;
    private final int maxSleepMs;
    private final Random random = new Random();

    /**
     * Constructor with default timeout values (100-1200ms).
     */
    protected SimulatedProviderClient() {
        this(DEFAULT_MIN_SLEEP_MS, DEFAULT_MAX_SLEEP_MS);
    }

    /**
     * Constructor with custom timeout values.
     * @param minSleepMs minimum sleep time in milliseconds
     * @param maxSleepMs maximum sleep time in milliseconds
     */
    protected SimulatedProviderClient(int minSleepMs, int maxSleepMs) {
        if (minSleepMs < 0 || maxSleepMs < minSleepMs) {
            throw new IllegalArgumentException(
                    "Invalid timeout range: min=" + minSleepMs + ", max=" + maxSleepMs);
        }
        this.minSleepMs = minSleepMs;
        this.maxSleepMs = maxSleepMs;
    }

    /**
     * Simulates network latency, then returns offers or throws.
     */
    @Override
    public final List<Offer> fetchOffers(String productId) {
        int sleepMs = minSleepMs + (maxSleepMs > minSleepMs ?
                random.nextInt(maxSleepMs - minSleepMs) : 0);
        try {
            Thread.sleep(sleepMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(providerId() + " interrupted", e);
        }

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

