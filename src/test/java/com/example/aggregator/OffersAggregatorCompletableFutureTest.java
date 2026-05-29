package com.example.aggregator;

import com.example.model.Offer;
import com.example.model.Request;
import com.example.provider.ProviderClient;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.example.aggregator.TestProviders.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for OffersAggregatorCompletableFuture.
 * Uses deterministic fake providers to keep tests stable and fast.
 */
class OffersAggregatorCompletableFutureTest {

    @Test
    void sortingAndTopN_returnsOffersOrderedByPriceThenEtaDays() {
        ProviderClient p1 = fixedProvider("p1",
                new Offer("p1", 100, 3),
                new Offer("p1", 50, 5));
        ProviderClient p2 = fixedProvider("p2",
                new Offer("p2", 50, 2),
                new Offer("p2", 200, 1));

        try (OffersAggregatorCompletableFuture agg =
                     new OffersAggregatorCompletableFuture(List.of(p1, p2))) {

            List<Offer> result = agg.findBestOffers(new Request("x", 2000), 3);

            assertEquals(3, result.size());
            // Sorted: price=50/eta=2, price=50/eta=5, price=100/eta=3
            assertEquals(50, result.get(0).price());
            assertEquals(2, result.get(0).etaDays());
            assertEquals(50, result.get(1).price());
            assertEquals(5, result.get(1).etaDays());
            assertEquals(100, result.get(2).price());
        }
    }

    @Test
    void topN_limitsResultCount() {
        ProviderClient p1 = fixedProvider("p1",
                new Offer("p1", 10, 1),
                new Offer("p1", 20, 1),
                new Offer("p1", 30, 1));

        try (OffersAggregatorCompletableFuture agg =
                     new OffersAggregatorCompletableFuture(List.of(p1))) {

            List<Offer> result = agg.findBestOffers(new Request("x", 2000), 2);

            assertEquals(2, result.size());
            assertEquals(10, result.get(0).price());
            assertEquals(20, result.get(1).price());
        }
    }

    @Test
    void failureIsolation_oneProviderThrows_othersStillReturn() {
        ProviderClient good = fixedProvider("good", new Offer("good", 99, 2));
        ProviderClient bad = failingProvider("bad");

        try (OffersAggregatorCompletableFuture agg =
                     new OffersAggregatorCompletableFuture(List.of(good, bad))) {

            List<Offer> result = agg.findBestOffers(new Request("x", 2000), 5);

            assertEquals(1, result.size());
            assertEquals("good", result.get(0).providerId());
        }
    }

    @Test
    void failureIsolation_allProvidersFail_returnsEmptyList() {
        ProviderClient bad1 = failingProvider("bad1");
        ProviderClient bad2 = failingProvider("bad2");

        try (OffersAggregatorCompletableFuture agg =
                     new OffersAggregatorCompletableFuture(List.of(bad1, bad2))) {

            List<Offer> result = agg.findBestOffers(new Request("x", 2000),5);

            assertTrue(result.isEmpty());
        }
    }

    @Test
    void timeout_slowProviderIgnored_fastProviderReturns() {
        ProviderClient fast = fixedProvider("fast", new Offer("fast", 42, 1));
        ProviderClient slow = slowProvider("slow", 5000);

        long timeoutMs = 500;

        try (OffersAggregatorCompletableFuture agg =
                     new OffersAggregatorCompletableFuture(List.of(fast, slow))) {

            long start = System.currentTimeMillis();
            List<Offer> result = agg.findBestOffers(new Request("x", timeoutMs),5);
            long elapsed = System.currentTimeMillis() - start;

            // Only fast provider's offer returned
            assertEquals(1, result.size());
            assertEquals("fast", result.get(0).providerId());

            // Method completed within timeout + reasonable overhead (500ms)
            assertTrue(elapsed < timeoutMs + 500,
                    "Elapsed " + elapsed + "ms exceeded budget");
        }
    }

    @Test
    void timeout_allSlowProviders_returnsEmpty() {
        ProviderClient slow1 = slowProvider("slow1", 5000);
        ProviderClient slow2 = slowProvider("slow2", 5000);

        long timeoutMs = 300;

        try (OffersAggregatorCompletableFuture agg =
                     new OffersAggregatorCompletableFuture(List.of(slow1, slow2))) {

            long start = System.currentTimeMillis();
            List<Offer> result = agg.findBestOffers(new Request("x", timeoutMs), 5);
            long elapsed = System.currentTimeMillis() - start;

            assertTrue(result.isEmpty());
            assertTrue(elapsed < timeoutMs + 500,
                    "Elapsed " + elapsed + "ms exceeded budget");
        }
    }
}

