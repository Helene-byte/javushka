package com.example.cache;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class GuavaLruCacheServiceTest {

    @Test
    void evictsLeastRecentlyUsedOnSizePressure() {
        RemovalLogListener listener = new RemovalLogListener();
        GuavaLruCacheService cache = new GuavaLruCacheService(2, Duration.ofSeconds(5), listener, new CacheStatistics());

        cache.put("a", new CacheEntry("A"));
        cache.put("b", new CacheEntry("B"));
        cache.get("a");
        cache.put("c", new CacheEntry("C"));

        assertEquals("A", cache.get("a").value());
        assertNull(cache.get("b"));
        assertEquals("C", cache.get("c").value());
    }
}

