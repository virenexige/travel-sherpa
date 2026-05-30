package com.aitravel.smartplanner.search;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelSearchResultRepository extends JpaRepository<TravelSearchResult, UUID> {
    List<TravelSearchResult> findTop50ByTravelWatch_IdOrderBySearchedAtDescDealScoreDesc(UUID travelWatchId);
    List<TravelSearchResult> findByTravelWatch_IdOrderByPackagePriceAsc(UUID travelWatchId);
}
