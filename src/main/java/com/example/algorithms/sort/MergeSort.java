package com.example.algorithms.sort;

import java.util.Arrays;

public final class MergeSort implements SortAlgorithm {
    @Override
    public int[] sort(int[] input) {
        int[] copy = Arrays.copyOf(input, input.length);
        int[] temp = new int[copy.length];
        mergeSort(copy, temp, 0, copy.length - 1);
        return copy;
    }

    @Override
    public String name() {
        return "merge-sort";
    }

    private void mergeSort(int[] arr, int[] temp, int left, int right) {
        if (left >= right) {
            return;
        }
        int mid = left + (right - left) / 2;
        mergeSort(arr, temp, left, mid);
        mergeSort(arr, temp, mid + 1, right);
        merge(arr, temp, left, mid, right);
    }

    private void merge(int[] arr, int[] temp, int left, int mid, int right) {
        int i = left;
        int j = mid + 1;
        int k = left;

        while (i <= mid && j <= right) {
            if (arr[i] <= arr[j]) {
                temp[k++] = arr[i++];
            } else {
                temp[k++] = arr[j++];
            }
        }

        while (i <= mid) {
            temp[k++] = arr[i++];
        }
        while (j <= right) {
            temp[k++] = arr[j++];
        }

        for (int idx = left; idx <= right; idx++) {
            arr[idx] = temp[idx];
        }
    }
}

