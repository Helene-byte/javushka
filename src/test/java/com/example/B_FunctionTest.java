package com.example;

import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class B_FunctionTest {

    @Test
    public void test_function_01() {
        Function<String, String> func = s -> "(" + s + ")";
        assertEquals("(abc)", func.apply("abc"));
        assertEquals("()", func.apply(""));

    }}

