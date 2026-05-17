package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MainTest {

    @Test
    void greetReturnsExpectedMessage() {
        assertEquals("Hello, Java!", Main.greet("Java"));
    }
}

