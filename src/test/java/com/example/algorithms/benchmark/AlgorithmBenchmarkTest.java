package com.example.algorithms.benchmark;

import com.example.algorithms.search.BinarySearch;
import com.example.algorithms.sort.InsertionSort;
import com.example.algorithms.sort.MergeSort;
import com.example.algorithms.sort.SortAlgorithm;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlgorithmBenchmarkTest {

    @Test
    void benchmarkSortAlgorithms() {
        int[] input = randomArray(1_500, 42);

        long mergeNs = runSortBenchmark(new MergeSort(), input, 20);
        long insertionNs = runSortBenchmark(new InsertionSort(), input, 20);

        int[] mergeSorted = new MergeSort().sort(input);
        int[] insertionSorted = new InsertionSort().sort(input);
        assertArrayEquals(mergeSorted, insertionSorted);
        assertTrue(mergeNs > 0);
        assertTrue(insertionNs > 0);

        System.out.println("merge-sort avg ns=" + mergeNs + ", insertion-sort avg ns=" + insertionNs);
    }

    @Test
    void benchmarkBinarySearchImplementations() {
        int[] sorted = new MergeSort().sort(randomArray(50_000, 7));
        int target = sorted[sorted.length / 2];

        long iterativeNs = runSearchBenchmark(sorted, target, true, 200_000);
        long recursiveNs = runSearchBenchmark(sorted, target, false, 200_000);

        assertTrue(iterativeNs > 0);
        assertTrue(recursiveNs > 0);
        System.out.println("binary-search iterative avg ns=" + iterativeNs + ", recursive avg ns=" + recursiveNs);
    }

    private static long runSortBenchmark(SortAlgorithm algorithm, int[] input, int rounds) {
        long total = 0;
        for (int i = 0; i < rounds; i++) {
            long start = System.nanoTime();
            algorithm.sort(input);
            total += System.nanoTime() - start;
        }
        return total / rounds;
    }

    private static long runSearchBenchmark(int[] sorted, int target, boolean iterative, int rounds) {
        long total = 0;
        for (int i = 0; i < rounds; i++) {
            long start = System.nanoTime();
            if (iterative) {
                BinarySearch.iterative(sorted, target);
            } else {
                BinarySearch.recursive(sorted, target);
            }
            total += System.nanoTime() - start;
        }
        return total / rounds;
    }

    private static int[] randomArray(int size, int seed) {
        Random random = new Random(seed);
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = random.nextInt();
        }
        return arr;
    }
}


