package com.example.algorithms.search;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BinarySearchTest {

    @Test
    void iterativeAndRecursiveFindExistingElement() {
        int[] sorted = {1, 3, 4, 7, 9, 11};

        int iterative = BinarySearch.iterative(sorted, 7);
        int recursive = BinarySearch.recursive(sorted, 7);

        assertEquals(3, iterative);
        assertEquals(3, recursive);
    }

    @Test
    void iterativeAndRecursiveReturnMinusOneWhenMissing() {
        int[] sorted = {1, 3, 4, 7, 9, 11};

        assertEquals(-1, BinarySearch.iterative(sorted, 8));
        assertEquals(-1, BinarySearch.recursive(sorted, 8));
    }
}

