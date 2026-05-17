package com.example.algorithms.search;

import com.example.algorithms.sort.SortAlgorithm;

import java.util.Objects;

public final class BinarySearchService {
    private final SortAlgorithm sorter;

    public BinarySearchService(SortAlgorithm sorter) {
        this.sorter = Objects.requireNonNull(sorter);
    }

    public int search(int[] input, int target) {
        int[] sorted = sorter.sort(input);
        return BinarySearch.iterative(sorted, target);
    }

    public int[] sortedCopy(int[] input) {
        return sorter.sort(input);
    }

    public String sortingStrategy() {
        return sorter.name();
    }
}

