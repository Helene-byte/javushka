package com.example.cache;

public interface Cache {
    CacheEntry get(String key);

    void put(String key, CacheEntry value);

    CacheStatsSnapshot stats();
}


