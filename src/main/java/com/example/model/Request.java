package com.example.model;

public record Request(String productId, long maxWaitMs) {
    // Compact constructor — runs before field assignment
    public Request {
        if (productId == null) {
            throw new IllegalArgumentException("productId must not be null");
        }
        if (maxWaitMs <= 0) {
            throw new IllegalArgumentException("maxWaitMs must be positive");
        }
    }
}
