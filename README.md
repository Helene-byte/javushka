# javushka-2

This project contains two cache service implementations and algorithm tasks (search/sort/tree traversal).

## Cache services

### 1) Simple Java cache (`LFU`)
- Class: `com.example.cache.LfuCacheService`
- Max size: `100_000` (configurable)
- Eviction policy:
  - LFU when size limit is reached
  - Expire-after-access: 5 seconds (configurable)
- Removal listener:
  - logs removed entries through `RemovalLogListener`
- Statistics:
  - average put time in nanoseconds
  - number of evictions
- Concurrency:
  - guarded with `ReentrantLock`

### 2) Guava cache (`LRU`)
- Class: `com.example.cache.GuavaLruCacheService`
- Uses Guava `CacheBuilder` with:
  - `maximumSize(100_000)`
  - `expireAfterAccess(5s)`
  - removal listener that writes to log
- Statistics:
  - average put time in nanoseconds
  - number of evictions
- Concurrency:
  - thread-safe by Guava design

## Algorithms

### Binary search
- `BinarySearch.iterative(...)`
- `BinarySearch.recursive(...)`
- Complexity: `O(log n)` time, `O(1)` extra space for iterative, `O(log n)` call stack for recursive.

### Merge sort
- `MergeSort`
- Complexity: `O(n log n)` time, `O(n)` extra space.

### Insertion sort
- `InsertionSort`
- Complexity:
  - best case `O(n)`
  - average/worst case `O(n^2)`
  - extra space `O(1)`

### Binary search + sorting integration
- `BinarySearchService` applies Strategy pattern (`SortAlgorithm`) to choose sorting algorithm before searching.

### Binary tree traversal (bypass)
- `BinaryTreeTraversal`:
  - preorder, inorder, postorder (recursive)
  - level-order (BFS)
  - inorder iterative

## Benchmarks

Benchmarks are in `AlgorithmBenchmarkTest`.

Observed tendency (from typical runs):
- Merge sort is much faster than insertion sort on large arrays.
- Iterative binary search is usually slightly faster than recursive due to no recursion overhead.

Use:

```bash
mvn -q -Dtest=AlgorithmBenchmarkTest test
```

## Build and test

```bash
mvn clean test
```

