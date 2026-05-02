package com.example;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PropStreamsTest {

    @Test
    void generateProducesExpectedCountNamesAndValues() {
        List<Prop> props = PropStreams.generate(3).toList();

        assertEquals(3, props.size());
        for (int i = 0; i < props.size(); i++) {
            assertNotNull(props.get(i).id());
            assertEquals("prop-" + i, props.get(i).name());
            assertEquals(i, props.get(i).value());
        }
    }

    @Test
    void toIdsFlattensNestedListsInOrder() {
        UUID id1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID id2 = UUID.fromString("00000000-0000-0000-0000-000000000002");
        UUID id3 = UUID.fromString("00000000-0000-0000-0000-000000000003");

        List<List<Prop>> nested = List.of(
                List.of(new Prop(id1, "a", 1)),
                List.of(new Prop(id2, "b", 2), new Prop(id3, "c", 3))
        );

        List<UUID> ids = PropStreams.toIds(nested).toList();

        assertIterableEquals(List.of(id1, id2, id3), ids);
    }

    @Test
    void countEvenCountsOnlyEvenNumbers() {
        long count = PropStreams.countEven(Stream.of(1, 2, 3, 4, 5, 6));
        assertEquals(3L, count);
    }

    @Test
    void firstWordReturnsFirstNonWhitespaceToken() {
        Stream<Character> chars = "   hello world".chars().mapToObj(c -> (char) c);

        String first = PropStreams.firstWord(chars)
                .map(String::valueOf)
                .collect(Collectors.joining());

        assertEquals("hello", first);
    }

    @Test
    void toPropertiesIncludesAllNonNullProperties() {
        UUID id1 = UUID.fromString("10000000-0000-0000-0000-000000000001");
        UUID id3 = UUID.fromString("30000000-0000-0000-0000-000000000003");

        List<String> properties = PropStreams.toProperties(Stream.of(
                new Prop(id1, "x", 1),
                new Prop(null, "y", 2),
                new Prop(id3, null, 3)
        )).toList();

        assertIterableEquals(
                List.of(id1.toString(), "x", "1", "y", "2", id3.toString(), "3"),
                properties
        );
    }

    @Test
    void sortByValueThenNameSortsCorrectly() {
        List<Prop> sorted = PropStreams.sortByValueThenName(Stream.of(
                new Prop(UUID.randomUUID(), "b", 2),
                new Prop(UUID.randomUUID(), "a", 2),
                new Prop(UUID.randomUUID(), "z", 1)
        ));

        assertEquals(1, sorted.get(0).value());
        assertEquals("z", sorted.get(0).name());
        assertEquals("a", sorted.get(1).name());
        assertEquals("b", sorted.get(2).name());
    }

    @Test
    void filterNonNullNameRemovesPropsWithNullName() {
        List<Prop> filtered = PropStreams.filterNonNullName(Stream.of(
                new Prop(UUID.randomUUID(), null, 1),
                new Prop(UUID.randomUUID(), "ok", 2)
        ));

        assertEquals(1, filtered.size());
        assertEquals("ok", filtered.get(0).name());
    }

    @Test
    void deduplicateByIdKeepsFirstOccurrencePerId() {
        UUID id = UUID.fromString("40000000-0000-0000-0000-000000000004");

        List<Prop> deduped = PropStreams.deduplicateById(Stream.of(
                new Prop(id, "first", 1),
                new Prop(id, "second", 2)
        )).toList();

        assertEquals(1, deduped.size());
        assertEquals("first", deduped.get(0).name());
    }

    @Test
    void nameWithHighestValueReturnsNameOfMaxItem() {
        String result = PropStreams.nameWithHighestValue(Stream.of(
                new Prop(UUID.randomUUID(), "low", 1),
                new Prop(UUID.randomUUID(), "high", 10)
        )).orElseThrow();

        assertEquals("high", result);
    }

    @Test
    void nameConflictsKeepsNamesWithDifferentIdsOnly() {
        UUID id1 = UUID.fromString("50000000-0000-0000-0000-000000000001");
        UUID id2 = UUID.fromString("50000000-0000-0000-0000-000000000002");

        Map<String, List<Prop>> conflicts = PropStreams.nameConflicts(Stream.of(
                new Prop(id1, "dup", 1),
                new Prop(id2, "dup", 2),
                new Prop(id1, "sameId", 3),
                new Prop(id1, "sameId", 4)
        ));

        assertEquals(1, conflicts.size());
        assertTrue(conflicts.containsKey("dup"));
        assertEquals(2, conflicts.get("dup").size());
    }

    @Test
    void sumByNameSumsValuesForSameName() {
        Map<String, Integer> result = PropStreams.sumByName(Stream.of(
                new Prop(UUID.randomUUID(), "a", 1),
                new Prop(UUID.randomUUID(), "a", 4),
                new Prop(UUID.randomUUID(), "b", 3)
        ));

        assertEquals(5, result.get("a"));
        assertEquals(3, result.get("b"));
    }

    @Test
    void sumOddEvenReturnsSumsForBothPartitions() {
        Map<Boolean, Integer> result = PropStreams.sumOddEven(Stream.of(1, 2, 3, 4));

        assertEquals(6, result.get(true));
        assertEquals(4, result.get(false));
    }

    @Test
    void foldComposesFunctionsLeftToRightAndEmptyIsIdentity() {
        Function<String, String> folded = PropStreams.fold(Stream.of(
                String::trim,
                s -> s.replace('b', 'c')
        ));

        assertEquals("cat", folded.apply("  bat"));

        Function<String, String> identity = PropStreams.fold(Stream.<Function<String, String>>empty());
        assertEquals(" test ", identity.apply(" test "));
    }

    @Test
    void partitioningCollectorSupportsDifferentCollectorsPerBranch() {
        record EvenSumOddCount(int evenSum, long oddCount) {
        }

        EvenSumOddCount result = Stream.of(1, 2, 3, 4)
                .collect(PropStreams.partitioningCollector(
                        n -> n % 2 == 0,
                        Collectors.summingInt(Integer::intValue),
                        Collectors.counting(),
                        EvenSumOddCount::new
                ));

        assertEquals(6, result.evenSum());
        assertEquals(2L, result.oddCount());
    }

    @Test
    void sumAndNullsReturnsSumAndNullCount() {
        PropStreams.SumAndNulls result = PropStreams.sumAndNulls(Stream.of(1, null, 2, null, -3));

        assertEquals(0, result.sum());
        assertEquals(2L, result.nullCount());
    }

    @Test
    void minMaxNamesReturnsLowestAndHighestNames() {
        PropStreams.MinMax result = PropStreams.minMaxNames(Stream.of(
                new Prop(UUID.randomUUID(), "low", -5),
                new Prop(UUID.randomUUID(), "mid", 0),
                new Prop(UUID.randomUUID(), "high", 9)
        )).orElseThrow();

        assertEquals("low", result.minName());
        assertEquals("high", result.maxName());
    }

    @Test
    void aggregationMethodsReturnEmptyForEmptyInput() {
        assertTrue(PropStreams.nameWithHighestValue(Stream.empty()).isEmpty());
        assertTrue(PropStreams.minMaxNames(Stream.empty()).isEmpty());
        assertFalse(PropStreams.nameConflicts(Stream.empty()).containsKey("any"));
    }
}

