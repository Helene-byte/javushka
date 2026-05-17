package com.example.algorithms.sort;

import java.util.Arrays;

public final class InsertionSort implements SortAlgorithm {
    @Override
    public int[] sort(int[] input) {
        int[] copy = Arrays.copyOf(input, input.length);
        for (int i = 1; i < copy.length; i++) {
            int key = copy[i];
            int j = i - 1;
            while (j >= 0 && copy[j] > key) {
                copy[j + 1] = copy[j];
                j--;
            }
            copy[j + 1] = key;
        }
        return copy;
    }

    @Override
    public String name() {
        return "insertion-sort";
    }
}

