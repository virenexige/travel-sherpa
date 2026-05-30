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
    LocalDate range2StartDate,
    LocalDate range2EndDate,
    LocalDate range3StartDate,
    LocalDate range3EndDate,
    int travellers,
    int flexibilityDays,
    int startDaysEarly,
    int startDaysLate,
    int finishDaysEarly,
    int finishDaysLate,
    int durationIncreaseDays,
    BigDecimal maxBudget,
    String tripType,
    Integer preferredHotelRating,
    String travelProductType,
    String cabinClass,
    boolean bucketList,
    String bucketListName,
    LocalDate earliestStartDate,
    LocalDate latestEndDate,
    String notes,
    TravelWatchStatus status,
    Instant createdAt,
    Instant updatedAt
) {
}
