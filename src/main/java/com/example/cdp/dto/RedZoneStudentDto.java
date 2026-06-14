package com.example.cdp.dto;

public record RedZoneStudentDto(
        Long studentId,
        String name,
        String surname,
        long lowMarkCount
) {
}
