# javushka-2 — Multithreading: Offer Aggregator

Fetches offers from multiple simulated providers concurrently and returns the best N results.  
Implemented twice: once with **CompletableFuture + ExecutorService**, once with **Java 21 virtual threads**.

---

## Project structure

```
src/main/java/com/example/
├── model/
│   ├── Offer.java             # providerId, price, etaDays
│   ├── Request.java           # productId, maxWaitMs
│   └── ProviderOutcome.java   # SUCCESS / FAILURE / TIMEOUT enum
├── provider/
│   ├── ProviderClient.java         # interface — pluggable for tests
│   ├── SimulatedProviderClient.java # base: random sleep 100–1200ms, 20% failure rate
│   ├── AmazonLikeClient.java
│   ├── LocalStoreClient.java
│   ├── EbayLikeClient.java
│   ├── WalmartLikeClient.java
│   └── ExpressDeliveryClient.java
├── aggregator/
│   ├── OffersAggregatorCompletableFuture.java  # implementation 1 — CF + fixed pool
│   └── OffersAggregatorVirtualThreads.java     # implementation 2 — virtual threads
└── app/
    └── Main.java   # console demo running both implementations
```

---

## How to run

### Prerequisites
- Java 21+
- Maven 3.8+

### Run tests
```bash
mvn clean test
```

### Run console demo (both aggregators)
```bash
mvn compile exec:java
```

---

## What timeouts and failures mean

### Timeout
A provider that does not respond within the **global `timeoutMs` budget** is **cancelled and ignored**.  
The aggregator returns only results from providers that finished in time.  
The method itself always returns within `timeoutMs + small scheduling overhead (~50–100ms)`.

### Failure
A provider that throws any exception (network error, bad data, etc.) is **silently ignored**.  
Other providers' results are unaffected. If all providers fail, an empty list is returned.

---

## CF vs Virtual Threads — differences observed

### Code style
- **CF**: callback-oriented — `supplyAsync`, `handle`, `allOf`. Readable but nested.
- **VT**: imperative blocking — `invokeAll` with timeout, plain `future.get()`. Much simpler to read and reason about.

### Timeout behaviour
- **CF**: `allOf(...).get(timeout)` waits for all futures; after timeout, futures are cancelled individually in a loop.  
  Each slow provider's sleep can block its platform thread until interrupted.
- **VT**: `invokeAll(tasks, timeout, unit)` enforces the deadline atomically across all tasks.  
  Cancelled virtual threads are extremely cheap to create and discard.

### Performance (observed in test run)
- CF timeout tests: **~10 seconds** (two slow providers, each sleeping 5s, waited somewhat sequentially before cancel propagation).
- VT timeout tests: **~0.85 seconds** — all tasks cancelled at the same deadline in one shot.

### Thread model
- **CF** uses a **fixed platform thread pool** — threads are expensive OS resources, pool size limits parallelism.
- **VT** uses **one virtual thread per task** — thousands of concurrent tasks with negligible memory overhead; JVM scheduler multiplexes them onto carrier threads automatically.

### When to prefer each
| Situation | Prefer |
|---|---|
| Java 8–17 (no virtual threads) | CompletableFuture |
| Many I/O-bound concurrent tasks (Java 21+) | Virtual threads |
| Complex async pipelines with transforms | CompletableFuture (`thenApply`, `thenCompose`) |
| Simple fan-out/gather with timeout | Virtual threads (`invokeAll`) |

