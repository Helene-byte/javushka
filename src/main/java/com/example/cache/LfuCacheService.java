package com.example.cache;

import java.time.Duration;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public final class LfuCacheService implements Cache {
    public static final int DEFAULT_MAX_SIZE = 100_000;
    public static final Duration DEFAULT_TTL = Duration.ofSeconds(5);

    private final int maxSize;
    private final long ttlNanos;
    private final RemovalLogListener removalLogListener;
    private final CacheStatistics statistics;
    private final ReentrantLock lock = new ReentrantLock();
    private final Map<String, Node> storage = new HashMap<>();

    public LfuCacheService(RemovalLogListener removalLogListener) {
        this(DEFAULT_MAX_SIZE, DEFAULT_TTL, removalLogListener, new CacheStatistics());
    }

    public LfuCacheService(int maxSize, Duration ttl, RemovalLogListener removalLogListener,
                           CacheStatistics statistics) {
        this.maxSize = maxSize;
        this.ttlNanos = ttl.toNanos();
        this.removalLogListener = Objects.requireNonNull(removalLogListener);
        this.statistics = Objects.requireNonNull(statistics);
    }

    @Override
    public CacheEntry get(String key) {
        Objects.requireNonNull(key);
        long now = System.nanoTime();
        lock.lock();
        try {
            Node node = storage.get(key);
            if (node == null) {
                return null;
            }
            if (isExpired(node, now)) {
                storage.remove(key);
                removeEntry(key, node, "EXPIRED", true);
                return null;
            }
            node.lastAccessNanos = now;
            node.frequency++;
            return node.value;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void put(String key, CacheEntry value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        long start = System.nanoTime();
        long now = start;
        lock.lock();
        try {
            evictExpired(now);
            Node existing = storage.get(key);
            if (existing != null) {
                existing.value = value;
                existing.lastAccessNanos = now;
                existing.frequency++;
            } else {
                if (storage.size() >= maxSize) {
                    evictLfu();
                }
                storage.put(key, new Node(value, now));
            }
        } finally {
            lock.unlock();
            statistics.recordPut(System.nanoTime() - start);
        }
    }

    public CacheStatsSnapshot stats() {
        return statistics.snapshot();
    }

    private void evictExpired(long now) {
        storage.entrySet().removeIf(entry -> {
            boolean expired = isExpired(entry.getValue(), now);
            if (expired) {
                removeEntry(entry.getKey(), entry.getValue(), "EXPIRED", true);
            }
            return expired;
        });
    }

    private void evictLfu() {
        storage.entrySet().stream()
                .min(Comparator
                        .comparingLong((Map.Entry<String, Node> e) -> e.getValue().frequency)
                        .thenComparingLong(e -> e.getValue().lastAccessNanos))
                .ifPresent(entry -> {
                    storage.remove(entry.getKey());
                    removeEntry(entry.getKey(), entry.getValue(), "SIZE", true);
                });
    }

    private boolean isExpired(Node node, long now) {

        return now - node.lastAccessNanos > ttlNanos;
    }

    private void removeEntry(String key, Node node, String cause, boolean evicted) {
        removalLogListener.logRemove(key, node.value, cause);
        if (evicted) {
            statistics.recordEviction();
        }
    }

    private static final class Node {
        private CacheEntry value;
        private long frequency;
        private long lastAccessNanos;

        private Node(CacheEntry value, long now) {
            this.value = value;
            this.lastAccessNanos = now;
            this.frequency = 1;
        }
    }
}

