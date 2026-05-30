package com.aitravel.smartplanner.tripwatch;

import com.aitravel.smartplanner.tripwatch.dto.TravelWatchResponse;

public final class TravelWatchMapper {
    private TravelWatchMapper() {
    }

    public static TravelWatchResponse toResponse(TravelWatch watch) {
        return new TravelWatchResponse(
            watch.getId(),
            watch.getUser().getId(),
            watch.getDepartureLocation(),
            watch.getDestination(),
            watch.getStartDate(),
            watch.getEndDate(),
            watch.getTravellers(),
            watch.getFlexibilityDays(),
            watch.getMaxBudget(),
            watch.getTripType(),
            watch.getPreferredHotelRating(),
            watch.getStatus(),
            watch.getCreatedAt(),
            watch.getUpdatedAt()
        );
    }
}
