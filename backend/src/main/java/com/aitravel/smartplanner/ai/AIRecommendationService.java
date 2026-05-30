package com.aitravel.smartplanner.ai;

import com.aitravel.smartplanner.recommendation.Recommendation;
import com.aitravel.smartplanner.search.TravelSearchResult;
import com.aitravel.smartplanner.tripwatch.TravelWatch;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AIRecommendationService {
    public List<Recommendation> generate(TravelWatch watch, List<TravelSearchResult> results) {
        if (results.isEmpty()) {
            return List.of();
        }
        TravelSearchResult best = results.stream()
            .min(Comparator.comparing(TravelSearchResult::getPackagePrice))
            .orElseThrow();
        Optional<TravelSearchResult> exact = results.stream()
            .filter(result -> result.getDestination().equalsIgnoreCase(watch.getDestination()))
            .filter(result -> result.getStartDate().equals(watch.getStartDate()))
            .filter(result -> result.getEndDate().equals(watch.getEndDate()))
            .findFirst();
        BigDecimal saving = exact.map(result -> result.getPackagePrice().subtract(best.getPackagePrice()).max(BigDecimal.ZERO))
            .orElse(BigDecimal.ZERO);
        String title = best.getDestination().equalsIgnoreCase(watch.getDestination())
            ? "Best date and airport option"
            : "Cheaper alternative destination";
        String explanation = "Your original " + watch.getDestination() + " plan is being monitored for "
            + watch.getTripType() + " travel. The current strongest option is " + best.getDestination()
            + " from " + best.getDepartureAirport() + " to " + best.getArrivalAirport()
            + " for " + best.getCurrency() + " " + best.getPackagePrice()
            + ". It balances price, convenience, hotel quality, and destination fit with a deal score of "
            + best.getDealScore() + ". Estimated saving versus the closest exact option is "
            + best.getCurrency() + " " + saving + ".";
        return List.of(new Recommendation(
            UUID.randomUUID(),
            watch,
            title,
            explanation,
            best.getDestination().equalsIgnoreCase(watch.getDestination()) ? "CHEAPER_DATE_WINDOW" : "ALTERNATIVE_DESTINATION",
            new BigDecimal(best.getDealScore()).divide(new BigDecimal("100")),
            saving,
            Instant.now()
        ));
    }
}
