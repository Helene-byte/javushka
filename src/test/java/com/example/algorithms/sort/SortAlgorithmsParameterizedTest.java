package com.example.algorithms.sort;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class SortAlgorithmsParameterizedTest {

    @ParameterizedTest
    @MethodSource("algorithms")
    void sortsIntegers(SortAlgorithm algorithm) {
        int[] sorted = algorithm.sort(new int[]{5, 2, 4, 1, 3});
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, sorted);
    }

    @ParameterizedTest
    @MethodSource("algorithms")
    void handlesAlreadySortedArray(SortAlgorithm algorithm) {
        int[] sorted = algorithm.sort(new int[]{1, 2, 3});
        assertArrayEquals(new int[]{1, 2, 3}, sorted);
    }

    private static Stream<SortAlgorithm> algorithms() {
        return Stream.of(new MergeSort(), new InsertionSort());
    }
}

