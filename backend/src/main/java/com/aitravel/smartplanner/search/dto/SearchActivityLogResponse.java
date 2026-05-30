package com.aitravel.smartplanner.search.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record SearchActivityLogResponse(
    UUID id,
    String providerName,
    String searchType,
    String departureLocation,
    String destination,
    String departureAirport,
    String arrivalAirport,
    LocalDate startDate,
    LocalDate endDate,
    String status,
    int offersReturned,
    BigDecimal cheapestPackagePrice,
    String currency,
    String message,
    Instant searchedAt
) {
}
