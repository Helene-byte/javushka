package com.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

public class A_PredicateTest {

    @Test
    void predicateTest() {
        Predicate<String> pred = s -> s.length() > 4;
        Assertions.assertTrue(pred.test("abcde"));
        Assertions.assertFalse(pred.test("abcd"));
        Assertions.assertFalse(pred.test(""));
    }

}
