package com.example;

import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class E_AdvancedCollectorsTest {

    @Test
    void partitioningCollectorSplitsIntoCustomAggregations() {
        record EvenSumOddCount(int evenSum, long oddCount) {}

        EvenSumOddCount result = Stream.of(1, 2, 3, 4)
                .collect(PropStreams.partitioningCollector(
                        n -> n % 2 == 0,
                        Collectors.summingInt(Integer::intValue),
                        Collectors.counting(),
                        EvenSumOddCount::new
                ));

        assertEquals(6, result.evenSum());
        assertEquals(2, result.oddCount());
    }

    @Test
    void sumAndNullsReturnsSumAndNullItemCount() {
        PropStreams.SumAndNulls result = PropStreams.sumAndNulls(Stream.of(1, null, 2, null, -3));

        assertEquals(0, result.sum());
        assertEquals(2L, result.nullCount());
    }
}

