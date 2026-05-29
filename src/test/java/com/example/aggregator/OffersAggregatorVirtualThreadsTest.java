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
 * Unit tests for OffersAggregatorVirtualThreads.
 * Mirror of the CF tests — both aggregators must behave identically.
 */
class OffersAggregatorVirtualThreadsTest {

    @Test
    void sortingAndTopN_returnsOffersOrderedByPriceThenEtaDays() {
        ProviderClient p1 = fixedProvider("p1",
                new Offer("p1", 100, 3),
                new Offer("p1", 50, 5));
        ProviderClient p2 = fixedProvider("p2",
                new Offer("p2", 50, 2),
                new Offer("p2", 200, 1));

        OffersAggregatorVirtualThreads agg =
                new OffersAggregatorVirtualThreads(List.of(p1, p2));

        List<Offer> result = agg.findBestOffers(new Request("x", 2000), 3);

        assertEquals(3, result.size());
        assertEquals(50, result.get(0).price());
        assertEquals(2, result.get(0).etaDays());
        assertEquals(50, result.get(1).price());
        assertEquals(5, result.get(1).etaDays());
        assertEquals(100, result.get(2).price());
    }

    @Test
    void topN_limitsResultCount() {
        ProviderClient p1 = fixedProvider("p1",
                new Offer("p1", 10, 1),
                new Offer("p1", 20, 1),
                new Offer("p1", 30, 1));

        OffersAggregatorVirtualThreads agg =
                new OffersAggregatorVirtualThreads(List.of(p1));

        List<Offer> result = agg.findBestOffers(new Request("x", 2000), 2);

        assertEquals(2, result.size());
        assertEquals(10, result.get(0).price());
        assertEquals(20, result.get(1).price());
    }

    @Test
    void failureIsolation_oneProviderThrows_othersStillReturn() {
        ProviderClient good = fixedProvider("good", new Offer("good", 99, 2));
        ProviderClient bad = failingProvider("bad");

        OffersAggregatorVirtualThreads agg =
                new OffersAggregatorVirtualThreads(List.of(good, bad));

        List<Offer> result = agg.findBestOffers(new Request("x", 2000), 5);

        assertEquals(1, result.size());
        assertEquals("good", result.get(0).providerId());
    }

    @Test
    void failureIsolation_allProvidersFail_returnsEmptyList() {
        OffersAggregatorVirtualThreads agg =
                new OffersAggregatorVirtualThreads(
                        List.of(failingProvider("bad1"), failingProvider("bad2")));

        assertTrue(agg.findBestOffers(new Request("x", 2000), 5).isEmpty());
    }

    @Test
    void timeout_slowProviderIgnored_fastProviderReturns() {
        ProviderClient fast = fixedProvider("fast", new Offer("fast", 42, 1));
        ProviderClient slow = slowProvider("slow", 5000);
        long timeoutMs = 500;

        OffersAggregatorVirtualThreads agg =
                new OffersAggregatorVirtualThreads(List.of(fast, slow));

        long start = System.currentTimeMillis();
        List<Offer> result = agg.findBestOffers(new Request("x", timeoutMs), 5);
        long elapsed = System.currentTimeMillis() - start;

        assertEquals(1, result.size());
        assertEquals("fast", result.get(0).providerId());
        assertTrue(elapsed < timeoutMs + 500,
                "Elapsed " + elapsed + "ms exceeded budget");
    }

    @Test
    void timeout_allSlowProviders_returnsEmpty() {
        OffersAggregatorVirtualThreads agg =
                new OffersAggregatorVirtualThreads(
                        List.of(slowProvider("slow1", 5000), slowProvider("slow2", 5000)));

        long timeoutMs = 300;
        long start = System.currentTimeMillis();
        List<Offer> result = agg.findBestOffers(new Request("x", timeoutMs), 5);
        long elapsed = System.currentTimeMillis() - start;

        assertTrue(result.isEmpty());
        assertTrue(elapsed < timeoutMs + 500,
                "Elapsed " + elapsed + "ms exceeded budget");
    }

    @Test
    void bothImplementationsReturnSameResultsForDeterministicProviders() {
        ProviderClient p1 = fixedProvider("p1",
                new Offer("p1", 80, 3),
                new Offer("p1", 30, 1));
        ProviderClient p2 = fixedProvider("p2",
                new Offer("p2", 50, 2));

        List<Offer> vtResult = new OffersAggregatorVirtualThreads(List.of(p1, p2))
                .findBestOffers(new Request("x", 2000), 5);

        try (OffersAggregatorCompletableFuture cfAgg =
                     new OffersAggregatorCompletableFuture(List.of(p1, p2))) {
            List<Offer> cfResult = cfAgg.findBestOffers(new Request("x", 2000), 5);
            assertEquals(cfResult, vtResult);
        }
    }
}

