package com.example.algorithms.search;

public final class BinarySearch {

    private BinarySearch() {
    }

    public static int iterative(int[] sorted, int target) {
        int left = 0;
        int right = sorted.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (sorted[mid] == target) {
                return mid;
            }
            if (sorted[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return -1;
    }

    public static int recursive(int[] sorted, int target) {

        return recursive(sorted, target, 0, sorted.length - 1);
    }

    private static int recursive(int[] sorted, int target, int left, int right) {
        if (left > right) {
            return -1;
        }
        int mid = left + (right - left) / 2;
        if (sorted[mid] == target) {
            return mid;
        }
        if (sorted[mid] < target) {
            return recursive(sorted, target, mid + 1, right);
        }
        return recursive(sorted, target, left, mid - 1);
    }
}

