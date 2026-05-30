package com.aitravel.smartplanner.tripwatch.dto;

import com.aitravel.smartplanner.tripwatch.TravelWatchStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TravelWatchResponse(
    UUID id,
    UUID userId,
    String departureLocation,
    String destination,
    LocalDate startDate,
    LocalDate endDate,
    int travellers,
    int flexibilityDays,
    BigDecimal maxBudget,
    String tripType,
    Integer preferredHotelRating,
    TravelWatchStatus status,
    Instant createdAt,
    Instant updatedAt
) {
}
