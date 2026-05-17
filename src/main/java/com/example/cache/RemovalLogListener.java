package com.example.cache;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class RemovalLogListener {
    private final Queue<String> entries = new ConcurrentLinkedQueue<>();

    public void logRemove(String key, CacheEntry value, String cause) {
        entries.add(cause + ":" + key + "=" + value.value());
    }

    public List<String> entries() {
        return List.copyOf(entries);
    }
}

