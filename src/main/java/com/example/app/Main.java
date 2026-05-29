package com.example.app;

import com.example.aggregator.OffersAggregatorCompletableFuture;
import com.example.aggregator.OffersAggregatorVirtualThreads;
import com.example.model.Offer;
import com.example.model.Request;
import com.example.provider.AmazonLikeClient;
import com.example.provider.EbayLikeClient;
import com.example.provider.ExpressDeliveryClient;
import com.example.provider.LocalStoreClient;
import com.example.provider.ProviderClient;
import com.example.provider.WalmartLikeClient;

import java.util.List;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        List<ProviderClient> providers = List.of(
                new AmazonLikeClient(),
                new LocalStoreClient(),
                new EbayLikeClient(),
                new WalmartLikeClient(),
                new ExpressDeliveryClient()
        );

        int topN = 3;
        Request request = new Request("laptop-pro-15", 1000);

        runCompletableFuture(providers, request, topN);
        System.out.println();
        runVirtualThreads(providers, request, topN);
    }

    private static void runCompletableFuture(
            List<ProviderClient> providers, Request request, int topN) {
        System.out.println("=== CompletableFuture Aggregator ===");
        System.out.printf("Product: %s | topN: %d | timeout: %dms%n%n", request.productId(), topN, request.maxWaitMs());

        try (OffersAggregatorCompletableFuture aggregator =
                     new OffersAggregatorCompletableFuture(providers)) {
            long start = System.currentTimeMillis();
            List<Offer> offers = aggregator.findBestOffers(request, topN);
            long elapsed = System.currentTimeMillis() - start;

            System.out.println(aggregator.stats());
            System.out.printf("Completed in %dms%n%n", elapsed);
            printOffers(offers);
        }
    }

    private static void runVirtualThreads(
            List<ProviderClient> providers, Request request, int topN) {
        System.out.println("=== Virtual Threads Aggregator ===");
        System.out.printf("Product: %s | topN: %d | timeout: %dms%n%n", request.productId(), topN, request.maxWaitMs());

        OffersAggregatorVirtualThreads aggregator =
                new OffersAggregatorVirtualThreads(providers);

        long start = System.currentTimeMillis();
        List<Offer> offers = aggregator.findBestOffers(request, topN);
        long elapsed = System.currentTimeMillis() - start;

        System.out.println(aggregator.stats());
        System.out.printf("Completed in %dms%n%n", elapsed);
        printOffers(offers);
    }

    private static void printOffers(List<Offer> offers) {
        if (offers.isEmpty()) {
            System.out.println("No offers returned.");
        } else {
            System.out.println("Top offers:");
            offers.forEach(o -> System.out.printf(
                    "  [%s] price=%d, etaDays=%d%n",
                    o.providerId(), o.price(), o.etaDays()));
        }
    }
}


