package com.example.cdp.dto;

import java.time.Instant;

public record AddressDto(
        Long id,
        Long studentId,
        String addressLine1,
        String addressLine2,
        String city,
        String postalCode,
        String country,
        Instant createdDatetime,
        Instant updatedDatetime
) {
}
