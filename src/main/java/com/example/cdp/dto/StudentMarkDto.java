package com.example.cdp.dto;

public record StudentMarkDto(
        Long studentId,
        String name,
        String surname,
        String subjectName,
        int mark
) {
}
