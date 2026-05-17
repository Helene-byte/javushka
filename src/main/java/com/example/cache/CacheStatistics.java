package com.example.cache;

import java.util.concurrent.atomic.AtomicLong;

public final class CacheStatistics {
    private final AtomicLong putCount = new AtomicLong();
    private final AtomicLong totalPutNanos = new AtomicLong();
    private final AtomicLong evictionCount = new AtomicLong();

    public void recordPut(long nanos) {
        putCount.incrementAndGet();
        totalPutNanos.addAndGet(nanos);
    }

    public void recordEviction() {

        evictionCount.incrementAndGet();
    }

    public CacheStatsSnapshot snapshot() {
        long count = putCount.get();
        double avg = count == 0 ? 0.0 : (double) totalPutNanos.get() / count;
        return new CacheStatsSnapshot(avg, evictionCount.get());
    }
}

