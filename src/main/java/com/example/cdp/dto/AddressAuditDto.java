package com.example.cdp.dto;

import java.time.Instant;

public record AddressAuditDto(
        Long id,
        Long originalAddressId,
        Long studentId,
        String addressLine1,
        String addressLine2,
        String city,
        String postalCode,
        String country,
        Instant changedAt,
        String operation
) {
}
