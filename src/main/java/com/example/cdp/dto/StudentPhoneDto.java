package com.example.cdp.dto;

public record StudentPhoneDto(
        Long studentId,
        String name,
        String surname,
        String phoneNumber,
        boolean primaryPhone
) {
}
