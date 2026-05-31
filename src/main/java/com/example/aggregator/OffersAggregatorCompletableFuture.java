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
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Aggregates offers from multiple providers concurrently using CompletableFuture.
 *
 * <p>Uses a dedicated fixed thread pool (not the common ForkJoinPool).
 * All providers are queried in parallel; a global timeout is applied.
 * Provider failures and timeouts are silently ignored — only successful results
 * are merged, sorted (price ASC, etaDays ASC) and the top N returned.
 */
public final class OffersAggregatorCompletableFuture implements AutoCloseable {

    private static final Comparator<Offer> OFFER_ORDER =
            Comparator.comparingInt(Offer::price).thenComparingInt(Offer::etaDays);

    private final List<ProviderClient> providers;
    private final ExecutorService executor;

    // Outcome counters across invocations, tracked by typed enum.
    private final Map<ProviderOutcome, AtomicInteger> outcomeCounts =
            new EnumMap<>(ProviderOutcome.class);

    /**
     * Creates aggregator with a fixed thread pool sized to number of providers.
     */
    public OffersAggregatorCompletableFuture(List<ProviderClient> providers) {
        this(providers, Executors.newFixedThreadPool(providers.size()));
    }

    /**
     * Creates aggregator with a custom executor (useful for testing).
     */
    public OffersAggregatorCompletableFuture(List<ProviderClient> providers, ExecutorService executor) {
        this.providers = List.copyOf(providers);
        this.executor = executor;
        for (ProviderOutcome outcome : ProviderOutcome.values()) {
            outcomeCounts.put(outcome, new AtomicInteger());
        }
    }

    /**
     * Queries all providers concurrently, waits up to timeoutMs, merges results.
     *
     * @param request record with product id to search for and global deadline in milliseconds
     * @param topN      maximum number of offers to return
     * @return sorted top-N offers from all providers that responded in time
     */
    public List<Offer> findBestOffers(Request request, int topN) {
        // One future per provider; failures/timeouts become empty lists
        List<CompletableFuture<List<Offer>>> futures = providers.stream()
                .map(provider -> CompletableFuture
                        .supplyAsync(() -> {
                            try {
                                return provider.fetchOffers(request.productId());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }, executor)
                        .handle((offers, ex) -> {
                            if (ex != null) {
                                recordOutcome(ex);
                                return List.<Offer>of();
                            }
                            outcomeCounts.get(ProviderOutcome.SUCCESS).incrementAndGet();
                            return offers;
                        }))
                .collect(Collectors.toList());

        // Wait for all futures up to global timeout
        CompletableFuture<Void> all = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0]));

        try {
            all.get(request.maxWaitMs(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            // Timeout or interruption: cancel incomplete futures, proceed with what we have
            futures.forEach(f -> f.cancel(true));
        }

        // Collect results from completed futures only
        List<Offer> collected = new ArrayList<>();
        for (int i = 0; i < futures.size(); i++) {
            CompletableFuture<List<Offer>> future = futures.get(i);
            if (future.isDone() && !future.isCancelled()) {
                try {
                    collected.addAll(future.getNow(List.of()));
                } catch (Exception e) {
                    outcomeCounts.get(ProviderOutcome.FAILURE).incrementAndGet();
                }
            } else if (future.isCancelled()) {
                outcomeCounts.get(ProviderOutcome.TIMEOUT).incrementAndGet();
            }
        }

        return collected.stream()
                .sorted(OFFER_ORDER)
                .limit(topN)
                .collect(Collectors.toList());
    }

    @Override
    public void close() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /** Returns a snapshot of per-provider outcome counts. */
    public AggregationStats stats() {
        int success = outcomeCounts.get(ProviderOutcome.SUCCESS).get();
        int failure = outcomeCounts.get(ProviderOutcome.FAILURE).get();
        int timeout = outcomeCounts.get(ProviderOutcome.TIMEOUT).get();
        return new AggregationStats(providers.size(), success, failure, timeout);
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

    private void recordOutcome(Throwable ex) {
        if (ex instanceof CancellationException
                || ex.getCause() instanceof InterruptedException) {
            outcomeCounts.get(ProviderOutcome.TIMEOUT).incrementAndGet();
        } else {
            outcomeCounts.get(ProviderOutcome.FAILURE).incrementAndGet();
        }
    }
}
