package com.example;

import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class C_ConsumerTest {

    @Test
    void c_consumer1() {
        StringBuilder sb = new StringBuilder("xyz");
        Consumer<StringBuilder> appenderConsumer = s -> s.append("abc");
        appenderConsumer.accept(sb);
        assertEquals("xyzabc", sb.toString());

    }
    }

