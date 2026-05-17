package com.example;

import com.example.algorithms.search.BinarySearchService;
import com.example.algorithms.sort.MergeSort;
import com.example.cache.CacheEntry;
import com.example.cache.Cache;
import com.example.cache.LfuCacheService;
import com.example.cache.RemovalLogListener;

public final class Main {

    private Main() {
    }

    public static String greet(String name) {
        return "Hello, " + name + "!";
    }

    public static void main(String[] args) {
        RemovalLogListener listener = new RemovalLogListener();
        Cache cache = new LfuCacheService(listener);
        cache.put("demo", new CacheEntry("value"));
        System.out.println(cache.get("demo"));

        BinarySearchService binarySearch = new BinarySearchService(new MergeSort());
        int index = binarySearch.search(new int[]{5, 1, 3, 2, 4}, 3);
        System.out.println("Index in sorted copy: " + index);
    }
}

