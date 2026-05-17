package com.example.cache;

import java.util.Objects;

public final class CacheEntry {
    private final String value;

    public CacheEntry(String value) {
        this.value = Objects.requireNonNull(value);
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return "CacheEntry{value='" + value + "'}";
    }
}

