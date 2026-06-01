package com.example.provider;

import com.example.model.Offer;

import java.util.List;

/**
 * Contract for any provider client.
 * Implementations may sleep, fail, or return multiple offers.
 */
public interface ProviderClient {

    /**
     * Unique identifier for this provider.
     */
    String providerId();

    /**
     * Fetch offers for the given product.
     * Implementations are allowed to throw exceptions to signal failures.
     *
     * @param productId product to look up
     * @return non-null list of offers (may be empty)
     * @throws RuntimeException if provider fails to return results
     */
    List<Offer> fetchOffers(String productId);
}

