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
    @Min(1) @Max(12) int travellers,
    @Min(0) @Max(14) int flexibilityDays,
    @DecimalMin("0.0") BigDecimal maxBudget,
    @NotBlank String tripType,
    @Min(1) @Max(5) Integer preferredHotelRating
) {
}
