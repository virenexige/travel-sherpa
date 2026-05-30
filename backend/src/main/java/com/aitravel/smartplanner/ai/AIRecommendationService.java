package com.aitravel.smartplanner.ai;

import com.aitravel.smartplanner.recommendation.Recommendation;
import com.aitravel.smartplanner.search.TravelSearchResult;
import com.aitravel.smartplanner.tripwatch.TravelWatch;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AIRecommendationService {
    private final McpTravelContextService mcpTravelContext;

    public AIRecommendationService(McpTravelContextService mcpTravelContext) {
        this.mcpTravelContext = mcpTravelContext;
    }

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
        String mcpContext = mcpTravelContext.destinationContext(watch)
            .map(context -> " Extra destination context from MCP: " + context)
            .orElse("");
        Recommendation dealRecommendation = new Recommendation(
            UUID.randomUUID(),
            watch,
            title,
            explanation + mcpContext,
            best.getDestination().equalsIgnoreCase(watch.getDestination()) ? "CHEAPER_DATE_WINDOW" : "ALTERNATIVE_DESTINATION",
            new BigDecimal(best.getDealScore()).divide(new BigDecimal("100")),
            saving,
            Instant.now()
        );
        return List.of(dealRecommendation, bookingTimingRecommendation(watch, best));
    }

    private Recommendation bookingTimingRecommendation(TravelWatch watch, TravelSearchResult best) {
        long daysUntilDeparture = ChronoUnit.DAYS.between(LocalDate.now(), watch.getStartDate());
        String advice;
        BigDecimal confidence;
        if (daysUntilDeparture > 180) {
            advice = "This is early for most leisure trips. Keep watching daily and book only if the package is well below your budget or matches a known school-holiday constraint.";
            confidence = new BigDecimal("0.72");
        } else if (daysUntilDeparture >= 60) {
            advice = "This is a good booking window for planned travel. If the current best option is within budget and scores above 80, booking now is reasonable; otherwise keep the watch active for another price cycle.";
            confidence = new BigDecimal("0.84");
        } else if (daysUntilDeparture >= 21) {
            advice = "Prices often become less forgiving inside two months. Book soon if the deal score is above 75 or the trip dates are not flexible.";
            confidence = new BigDecimal("0.80");
        } else {
            advice = "This is a late booking window. Waiting is higher risk unless your dates and airports are very flexible.";
            confidence = new BigDecimal("0.76");
        }
        String scope = watch.isBucketList()
            ? " For this bucket-list watch, the app will keep checking daily for cheaper flights and hotels."
            : " Keep the watch active to compare future drops against the historical low.";
        return new Recommendation(UUID.randomUUID(), watch, "When to book", advice + scope
            + " Current best package is " + best.getCurrency() + " " + best.getPackagePrice()
            + " with a deal score of " + best.getDealScore() + ".", "BOOKING_TIMING",
            confidence, BigDecimal.ZERO, Instant.now());
    }
}
