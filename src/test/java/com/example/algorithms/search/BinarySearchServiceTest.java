package com.example.algorithms.search;

import com.example.algorithms.sort.InsertionSort;
import com.example.algorithms.sort.MergeSort;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BinarySearchServiceTest {

    @Test
    void searchWorksWhenInputIsUnsortedUsingMergeSort() {
        BinarySearchService service = new BinarySearchService(new MergeSort());

        int idx = service.search(new int[]{9, 2, 1, 8, 7}, 8);

        assertEquals(3, idx);
        assertEquals("merge-sort", service.sortingStrategy());
    }

    @Test
    void searchWorksWhenInputIsUnsortedUsingInsertionSort() {
        BinarySearchService service = new BinarySearchService(new InsertionSort());

        int idx = service.search(new int[]{5, 4, 3, 2, 1}, 4);

        assertEquals(3, idx);
    }

    @Test
    void sortedCopyDoesNotMutateInput() {
        BinarySearchService service = new BinarySearchService(new MergeSort());
        int[] input = {3, 1, 2};

        int[] sorted = service.sortedCopy(input);

        assertArrayEquals(new int[]{1, 2, 3}, sorted);
        assertArrayEquals(new int[]{3, 1, 2}, input);
    }
}

