package com.aitravel.smartplanner.search.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record SearchResultResponse(
    UUID id,
    String providerName,
    String destination,
    String departureAirport,
    String arrivalAirport,
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal flightPrice,
    BigDecimal hotelPrice,
    BigDecimal packagePrice,
    String currency,
    int dealScore,
    String resultUrl,
    Instant searchedAt
) {
}
