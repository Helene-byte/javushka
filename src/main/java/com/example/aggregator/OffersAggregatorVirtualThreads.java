package com.example.aggregator;

import com.example.model.Offer;
import com.example.model.ProviderOutcome;
import com.example.model.Request;
import com.example.provider.ProviderClient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Aggregates offers from multiple providers concurrently using Java 21 virtual threads.
 *
 * <p>One virtual thread is spawned per provider via {@code Executors.newVirtualThreadPerTaskExecutor()}.
 * Global timeout is enforced with {@code invokeAll(tasks, timeout, unit)}.
 * Provider failures and timeouts are silently ignored.
 * Results are merged, sorted (price ASC, etaDays ASC) and the top N returned.
 */
public final class OffersAggregatorVirtualThreads {

    private static final Comparator<Offer> OFFER_ORDER =
            Comparator.comparingInt(Offer::price).thenComparingInt(Offer::etaDays);

    private final List<ProviderClient> providers;

    // Outcome counters are cumulative across all findBestOffers calls.
    private final Map<ProviderOutcome, AtomicInteger> outcomeCounts =
            new EnumMap<>(ProviderOutcome.class);

    public OffersAggregatorVirtualThreads(List<ProviderClient> providers) {
        this.providers = List.copyOf(providers);
        for (ProviderOutcome outcome : ProviderOutcome.values()) {
            outcomeCounts.put(outcome, new AtomicInteger());
        }
    }

    /**
     * Queries all providers concurrently on virtual threads, waits up to timeoutMs,
     * merges results.
     *
     * @param request record with product id to search for and global deadline in milliseconds
     * @param topN      maximum number of offers to return
     * @return sorted top-N offers from all providers that responded in time
     */
    public List<Offer> findBestOffers(Request request, int topN) {
        // Build one Callable per provider — plain blocking code, virtual threads make it cheap
        List<Callable<List<Offer>>> tasks = providers.stream()
                .<Callable<List<Offer>>>map(provider -> () -> provider.fetchOffers(request.productId()))
                .collect(Collectors.toList());

        List<Offer> collected = new ArrayList<>();

        // try-with-resources ensures virtual thread executor is closed (threads drained)
        try (ExecutorService vte = Executors.newVirtualThreadPerTaskExecutor()) {
            // invokeAll blocks until all tasks finish OR timeout expires;
            // tasks that didn't finish in time are cancelled
            List<Future<List<Offer>>> futures =
                    vte.invokeAll(tasks, request.maxWaitMs(), TimeUnit.MILLISECONDS);

            for (int i = 0; i < futures.size(); i++) {
                Future<List<Offer>> future = futures.get(i);
                if (future.isCancelled()) {
                    outcomeCounts.get(ProviderOutcome.TIMEOUT).incrementAndGet();
                } else {
                    try {
                        List<Offer> result = future.get();
                        collected.addAll(result);
                        outcomeCounts.get(ProviderOutcome.SUCCESS).incrementAndGet();
                    } catch (Exception e) {
                        outcomeCounts.get(ProviderOutcome.FAILURE).incrementAndGet();
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return collected.stream()
                .sorted(OFFER_ORDER)
                .limit(topN)
                .collect(Collectors.toList());
    }

    /** Returns cumulative outcome counts across all findBestOffers calls. */
    public AggregationStats stats() {
        return new AggregationStats(
                providers.size(),
                outcomeCounts.get(ProviderOutcome.SUCCESS).get(),
                outcomeCounts.get(ProviderOutcome.FAILURE).get(),
                outcomeCounts.get(ProviderOutcome.TIMEOUT).get());
    }

    /** Immutable snapshot of aggregation metrics. */
    public record AggregationStats(int totalProviders, int succeeded, int failed, int timedOut) {
        @Override
        public String toString() {
            return String.format(
                    "AggregationStats{total=%d, succeeded=%d, failed=%d, timedOut=%d}",
                    totalProviders, succeeded, failed, timedOut);
        }
    }
}
