package com.example.cache;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class CacheConcurrencyTest {

    @Test
    void lfuSupportsConcurrentGetPut() {
        LfuCacheService cache = new LfuCacheService(1000, Duration.ofSeconds(5),
                new RemovalLogListener(), new CacheStatistics());
        runConcurrentScenario(cache);
    }

    @Test
    void guavaSupportsConcurrentGetPut() {
        GuavaLruCacheService cache = new GuavaLruCacheService(1000, Duration.ofSeconds(5),
                new RemovalLogListener(), new CacheStatistics());
        runConcurrentScenario(cache);
    }

    private void runConcurrentScenario(Cache cache) {
        ExecutorService pool = Executors.newFixedThreadPool(8);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<?>> futures = new ArrayList<>();

        for (int t = 0; t < 8; t++) {
            final int thread = t;
            futures.add(pool.submit(() -> {
                start.await();
                for (int i = 0; i < 1000; i++) {
                    String key = "k-" + (thread * 1000 + i);
                    cache.put(key, new CacheEntry("v-" + i));
                    cache.get(key);
                }
                return null;
            }));
        }

        start.countDown();
        for (Future<?> future : futures) {
            assertDoesNotThrow(() -> {
                future.get();
            });
        }
        pool.shutdownNow();
    }
}


