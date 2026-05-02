package com.example;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.Stream;

record Prop(UUID id, String name, int value) {
}

public class PropStreams {

    private PropStreams() {
    }
    // Produces n props: random id, value = 0..n-1
    public static Stream<Prop> generate(int n) {
        return IntStream.range(0, n)
                .mapToObj(i -> new Prop(
                        UUID.randomUUID(),
                        "prop-" + i,   // name can be anything; this keeps it deterministic
                        i
                ));
    }

    // Flattens nested lists and returns only ids
    public static Stream<UUID> toIds(List<List<Prop>> nested) {
        return nested.stream()
                .flatMap(List::stream)
                .map(Prop::id);
    }
    //counts
    public static long countEven(Stream<Integer> stream) {
        return stream.filter(n -> n % 2 == 0).count();
    }

    //shortening operations, returns Stream<Character> that contains the first word from the original stream
    public static Stream<Character> firstWord(Stream<Character> stream) {
        String word = stream
                .map(String::valueOf)
                .collect(Collectors.joining())
                .stripLeading()
                .split("\\s+", 2)[0];
        return word.chars().mapToObj(c -> (char) c);
    }
    //indirect mapping, The result stream should contain all non-null properties of the objects
    public static Stream<String> toProperties(Stream<Prop> stream) {
        return stream.flatMap(p -> Stream.of(
                        p.id() != null ? p.id().toString() : null,
                        p.name() != null ? p.name() : null,
                        String.valueOf(p.value())
                ).filter(Objects::nonNull)
        );
    }
    //sorting,  returns a List<Prop> sorted by value property first then by name
    public static List<Prop> sortByValueThenName(Stream<Prop> stream) {
        return stream
                .sorted(Comparator.comparingInt(Prop::value)
                        .thenComparing(Prop::name))
                .collect(Collectors.toList());
    }
   //filter by property ,  returns a List<Prop> sorted by value property first then by name
    public static List<Prop> filterNonNullName(Stream<Prop> stream) {
        return stream
                .filter(p -> p.name() != null)
                .collect(Collectors.toList());
    }
   //stateful filter
    public static Stream<Prop> deduplicateById(Stream<Prop> stream) {
        Set<UUID> seen = new HashSet<>();
        return stream.filter(p -> seen.add(p.id()));
    }
   //aggregation
    public static Optional<String> nameWithHighestValue(Stream<Prop> stream) {
        return stream
                .max(Comparator.comparingInt(Prop::value))
                .map(Prop::name);
    }
   //combining Collectors
    public static Map<String, List<Prop>> nameConflicts(Stream<Prop> stream) {
        return stream
                .collect(Collectors.groupingBy(Prop::name))
                .entrySet().stream()
                .filter(e -> e.getValue().stream()
                        .map(Prop::id)
                        .distinct()
                        .count() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
  //stateful collectors
    public static Map<String, Integer> sumByName(Stream<Prop> stream) {
        return stream.collect(
                Collectors.groupingBy(Prop::name, Collectors.summingInt(Prop::value)));
    }
  //collector chaining
    public static Map<Boolean, Integer> sumOddEven(Stream<Integer> stream) {
        return stream.collect(
                Collectors.partitioningBy(
                        n -> n % 2 == 0,
                        Collectors.summingInt(Integer::intValue)
                )
        );
    }

    //custom aggregation
    public record MinMax(String minName, String maxName) {
    }

    public static Optional<MinMax> minMaxNames(Stream<Prop> stream) {
        return stream.collect(
                Collectors.teeing(
                        Collectors.minBy(Comparator.comparingInt(Prop::value)),
                        Collectors.maxBy(Comparator.comparingInt(Prop::value)),
                        (min, max) -> min.flatMap(
                                lo -> max.map(
                                        hi -> new MinMax(lo.name(), hi.name())))
                )
        );
    }

    // Functional Transformation Applies provided functions in sequence;
    // empty stream -> identity.
    public static <T> Function<T, T> fold(Stream<Function<T, T>> functions) {
        return functions.reduce(Function.identity(), Function::andThen);
    }

    // advanced collector returns a 'partitioning' Collector that splits the stream based
    // on the provided predicate
    public static <T, RT, AT, RF, AF, R> Collector<T, ?, R> partitioningCollector(
            Predicate<? super T> predicate,
            Collector<? super T, AT, RT> collTrue,
            Collector<? super T, AF, RF> collFalse,
            BiFunction<RT, RF, R> constructor
    ) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(collTrue);
        Objects.requireNonNull(collFalse);
        Objects.requireNonNull(constructor);

        class PartitionAccumulator {
            private final AT trueState;
            private final AF falseState;

            private PartitionAccumulator(AT trueState, AF falseState) {
                this.trueState = trueState;
                this.falseState = falseState;
            }
        }

        return Collector.of(
                () -> new PartitionAccumulator(collTrue.supplier().get(), collFalse.supplier().get()),
                (acc, item) -> {
                    if (predicate.test(item)) {
                        collTrue.accumulator().accept(acc.trueState, item);
                    } else {
                        collFalse.accumulator().accept(acc.falseState, item);
                    }
                },
                (left, right) -> new PartitionAccumulator(
                        collTrue.combiner().apply(left.trueState, right.trueState),
                        collFalse.combiner().apply(left.falseState, right.falseState)
                ),
                acc -> constructor.apply(
                        collTrue.finisher().apply(acc.trueState),
                        collFalse.finisher().apply(acc.falseState)
                )
        );
    }



    public record SumAndNulls(int sum, long nullCount) {
    }

    public static SumAndNulls sumAndNulls(Stream<Integer> stream) {
        return stream.collect(
                partitioningCollector(
                        Objects::nonNull,
                        Collectors.summingInt(Integer::intValue),
                        Collectors.counting(),
                        SumAndNulls::new
                )
        );
    }
}