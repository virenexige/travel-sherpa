package com.aitravel.smartplanner.search;

import java.time.LocalDate;
import java.util.UUID;

public record SearchCriteria(
    UUID travelWatchId,
    String departureLocation,
    String destination,
    String departureAirport,
    String arrivalAirport,
    LocalDate startDate,
    LocalDate endDate,
    int travellers,
    String tripType,
    Integer preferredHotelRating,
    String travelProductType,
    String cabinClass,
    boolean exactMatch
) {
}
