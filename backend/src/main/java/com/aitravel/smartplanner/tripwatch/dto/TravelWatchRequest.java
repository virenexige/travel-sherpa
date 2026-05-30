package com.aitravel.smartplanner.tripwatch.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TravelWatchRequest(
    @NotBlank String departureLocation,
    @NotBlank String destination,
    @NotNull @FutureOrPresent LocalDate startDate,
    @NotNull @FutureOrPresent LocalDate endDate,
    @FutureOrPresent LocalDate range2StartDate,
    @FutureOrPresent LocalDate range2EndDate,
    @FutureOrPresent LocalDate range3StartDate,
    @FutureOrPresent LocalDate range3EndDate,
    @Min(1) @Max(12) int travellers,
    @Min(0) @Max(14) int flexibilityDays,
    @Min(0) @Max(14) int startDaysEarly,
    @Min(0) @Max(14) int startDaysLate,
    @Min(0) @Max(14) int finishDaysEarly,
    @Min(0) @Max(14) int finishDaysLate,
    @Min(0) @Max(14) int durationIncreaseDays,
    @DecimalMin("0.0") BigDecimal maxBudget,
    @NotBlank String tripType,
    @Min(1) @Max(5) Integer preferredHotelRating,
    String travelProductType,
    String cabinClass,
    boolean bucketList,
    String bucketListName,
    LocalDate earliestStartDate,
    LocalDate latestEndDate,
    String notes
) {
}
