package com.aitravel.smartplanner.pricing;

import com.aitravel.smartplanner.provider.ProviderOffer;
import com.aitravel.smartplanner.search.SearchCriteria;
import com.aitravel.smartplanner.tripwatch.TravelWatch;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Service;

@Service
public class DealScoreService {
    public int score(TravelWatch watch, SearchCriteria criteria, ProviderOffer offer) {
        int priceScore = priceScore(watch, offer.packagePrice());
        int dateFlexibilityScore = criteria.exactMatch() ? 15 : 11;
        int destinationMatchScore = criteria.destination().equalsIgnoreCase(watch.getDestination()) ? 15 : 10;
        int convenienceScore = criteria.departureAirport().equalsIgnoreCase(watch.getDepartureLocation()) ? 18 : 15;
        int hotelQualityScore = watch.getPreferredHotelRating() == null ? 8 : Math.min(10, watch.getPreferredHotelRating() * 2);
        return Math.min(100, priceScore + dateFlexibilityScore + destinationMatchScore + convenienceScore + hotelQualityScore);
    }

    private int priceScore(TravelWatch watch, BigDecimal packagePrice) {
        if (watch.getMaxBudget() == null || watch.getMaxBudget().compareTo(BigDecimal.ZERO) <= 0) {
            return 30;
        }
        BigDecimal ratio = packagePrice.divide(watch.getMaxBudget(), 4, RoundingMode.HALF_UP);
        if (ratio.compareTo(new BigDecimal("0.75")) <= 0) return 40;
        if (ratio.compareTo(BigDecimal.ONE) <= 0) return 34;
        if (ratio.compareTo(new BigDecimal("1.15")) <= 0) return 24;
        return 14;
    }
}
