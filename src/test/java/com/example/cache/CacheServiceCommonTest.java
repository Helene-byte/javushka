package com.example.cache;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CacheServiceCommonTest {

    @ParameterizedTest
    @MethodSource("cacheFactories")
    void putAndGetWorksForBothImplementations(String name, CacheHarness harness) {
        harness.cache().put("k1", new CacheEntry("v1"));

        CacheEntry found = harness.cache().get("k1");

        assertNotNull(found, name);
        assertEquals("v1", found.value());
        assertTrue(harness.cache().stats().averagePutNanos() >= 0.0);
    }

    @ParameterizedTest
    @MethodSource("cacheFactories")
    void expireAfterAccessEvictsEntry(String name, CacheHarness harness) throws InterruptedException {
        harness.cache().put("temp", new CacheEntry("x"));

        Thread.sleep(170);

        CacheEntry found = harness.cache().get("temp");

        assertNull(found, name);
        assertTrue(harness.cache().stats().evictionCount() >= 1, name);
        assertFalseLogIsEmpty(name, harness.listener());
    }

    @ParameterizedTest
    @MethodSource("cacheFactories")
    void statsTracksPutAverageAndEvictions(String name, CacheHarness harness) throws InterruptedException {
        harness.cache().put("a", new CacheEntry("1"));
        harness.cache().put("b", new CacheEntry("2"));

        Thread.sleep(170);
        harness.cache().get("a");

        CacheStatsSnapshot snapshot = harness.cache().stats();
        assertTrue(snapshot.averagePutNanos() > 0.0, name);
        assertTrue(snapshot.evictionCount() >= 1, name);
    }

    private static void assertFalseLogIsEmpty(String name, RemovalLogListener listener) {
        assertTrue(!listener.entries().isEmpty(), name);
    }

    private static Stream<Arguments> cacheFactories() {
        RemovalLogListener lfuListener = new RemovalLogListener();
        RemovalLogListener guavaListener = new RemovalLogListener();
        return Stream.of(
                Arguments.of("lfu", new CacheHarness(
                        new LfuCacheService(3, Duration.ofMillis(120), lfuListener, new CacheStatistics()),
                        lfuListener
                )),
                Arguments.of("guava", new CacheHarness(
                        new GuavaLruCacheService(3, Duration.ofMillis(120), guavaListener, new CacheStatistics()),
                        guavaListener
                ))
        );
    }

    private record CacheHarness(Cache cache, RemovalLogListener listener) {
    }
}



