package com.aitravel.smartplanner.search;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchActivityLogRepository extends JpaRepository<SearchActivityLog, UUID> {
    List<SearchActivityLog> findTop100ByTravelWatch_IdOrderBySearchedAtDesc(UUID travelWatchId);
    List<SearchActivityLog> findTop20ByTravelWatch_IdAndSearchTypeOrderBySearchedAtDesc(UUID travelWatchId, String searchType);
}
