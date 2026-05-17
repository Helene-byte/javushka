package com.example.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public final class GuavaLruCacheService implements Cache {
    public static final int DEFAULT_MAX_SIZE = 100_000;
    public static final Duration DEFAULT_TTL = Duration.ofSeconds(5);

    private final com.google.common.cache.Cache<String, CacheEntry> cache;
    private final CacheStatistics statistics;
    private final RemovalLogListener removalLogListener;

    public GuavaLruCacheService(RemovalLogListener removalLogListener) {
        this(DEFAULT_MAX_SIZE, DEFAULT_TTL, removalLogListener, new CacheStatistics());
    }

    public GuavaLruCacheService(int maxSize, Duration ttl, RemovalLogListener removalLogListener,
                                CacheStatistics statistics) {
        this.statistics = Objects.requireNonNull(statistics);
        Objects.requireNonNull(removalLogListener);
        RemovalListener<String, CacheEntry> listener = this::handleRemoval;
        this.cache = CacheBuilder.newBuilder()
                .removalListener(listener)
                .maximumSize(maxSize)
                .expireAfterAccess(ttl.toNanos(), TimeUnit.NANOSECONDS)
                .build();
        this.removalLogListener = removalLogListener;
    }

    @Override
    public CacheEntry get(String key) {
        Objects.requireNonNull(key);
        return cache.getIfPresent(key);
    }

    @Override
    public void put(String key, CacheEntry value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        long start = System.nanoTime();
        cache.put(key, value);
        statistics.recordPut(System.nanoTime() - start);
    }

    public CacheStatsSnapshot stats() {
        cache.cleanUp();
        return statistics.snapshot();
    }



    private void handleRemoval(RemovalNotification<String, CacheEntry> notification) {
        CacheEntry removed = notification.getValue();
        if (removed == null) {
            return;
        }
        RemovalCause cause = notification.getCause();
        removalLogListener.logRemove(notification.getKey(), removed, cause.name());
        if (cause != RemovalCause.EXPLICIT && cause != RemovalCause.REPLACED) {
            statistics.recordEviction();
        }
    }
}

