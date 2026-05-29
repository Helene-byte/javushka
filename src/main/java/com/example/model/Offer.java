package com.example.model;

import java.util.Objects;

public record Offer(String providerId, int price, int etaDays) {
    // Compact constructor — runs before field assignment
    public Offer {
        Objects.requireNonNull(providerId, "providerId must not be null");
        if (price < 0) {
            throw new IllegalArgumentException("price must be non-negative");
        }
        if (etaDays < 0) {
            throw new IllegalArgumentException("etaDays must be non-negative");
        }
    }
}
