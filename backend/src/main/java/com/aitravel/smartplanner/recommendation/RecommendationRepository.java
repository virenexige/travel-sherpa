package com.aitravel.smartplanner.recommendation;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendationRepository extends JpaRepository<Recommendation, UUID> {
    List<Recommendation> findTop10ByTravelWatch_IdOrderByCreatedAtDesc(UUID travelWatchId);
}
