package com.aitravel.smartplanner.tripwatch;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelWatchRepository extends JpaRepository<TravelWatch, UUID> {
    List<TravelWatch> findByUser_EmailOrderByCreatedAtDesc(String email);
    Optional<TravelWatch> findByIdAndUser_Email(UUID id, String email);
    List<TravelWatch> findByStatus(TravelWatchStatus status);
}
