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
            watch.getRange2StartDate(),
            watch.getRange2EndDate(),
            watch.getRange3StartDate(),
            watch.getRange3EndDate(),
            watch.getTravellers(),
            watch.getFlexibilityDays(),
            watch.getStartDaysEarly(),
            watch.getStartDaysLate(),
            watch.getFinishDaysEarly(),
            watch.getFinishDaysLate(),
            watch.getDurationIncreaseDays(),
            watch.getTripDurationDays(),
            watch.getMaxBudget(),
            watch.getTripType(),
            watch.getPreferredHotelRating(),
            watch.getTravelProductType(),
            watch.getCabinClass(),
            watch.isBucketList(),
            watch.getBucketListName(),
            watch.getEarliestStartDate(),
            watch.getLatestEndDate(),
            watch.getNotes(),
            watch.getStatus(),
            watch.getCreatedAt(),
            watch.getUpdatedAt()
        );
    }
}
