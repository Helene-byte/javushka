package com.example;

import java.util.*;
import java.util.stream.Collectors;
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


    //custom agregation
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



}