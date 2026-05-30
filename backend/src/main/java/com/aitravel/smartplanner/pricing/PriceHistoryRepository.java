package com.aitravel.smartplanner.pricing;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, UUID> {
    List<PriceHistory> findByTravelWatch_IdOrderBySearchedAtAsc(UUID travelWatchId);

    @Query("select min(p.packagePrice) from PriceHistory p where p.travelWatch.id = :travelWatchId")
    Optional<BigDecimal> findLowestPackagePrice(@Param("travelWatchId") UUID travelWatchId);

    @Query("select min(p.flightPrice) from PriceHistory p where p.travelWatch.id = :travelWatchId")
    Optional<BigDecimal> findLowestFlightPrice(@Param("travelWatchId") UUID travelWatchId);

    @Query("select min(p.hotelPrice) from PriceHistory p where p.travelWatch.id = :travelWatchId")
    Optional<BigDecimal> findLowestHotelPrice(@Param("travelWatchId") UUID travelWatchId);
}
