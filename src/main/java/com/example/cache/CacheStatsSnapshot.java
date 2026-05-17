package com.example.cache;

public record CacheStatsSnapshot(double averagePutNanos, long evictionCount) {
}

