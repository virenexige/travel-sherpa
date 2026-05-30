package com.aitravel.smartplanner.pricing;

import com.aitravel.smartplanner.notification.NotificationService;
import com.aitravel.smartplanner.search.TravelSearchResult;
import com.aitravel.smartplanner.tripwatch.TravelWatch;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PriceDropDetector {
    private final PriceHistoryRepository priceHistory;
    private final NotificationService notifications;
    private final BigDecimal packageThreshold;
    private final BigDecimal flightThreshold;
    private final BigDecimal hotelThreshold;

    public PriceDropDetector(
        PriceHistoryRepository priceHistory,
        NotificationService notifications,
        @Value("${app.alerts.package-drop-threshold}") BigDecimal packageThreshold,
        @Value("${app.alerts.flight-drop-threshold}") BigDecimal flightThreshold,
        @Value("${app.alerts.hotel-drop-threshold}") BigDecimal hotelThreshold) {
        this.priceHistory = priceHistory;
        this.notifications = notifications;
        this.packageThreshold = packageThreshold;
        this.flightThreshold = flightThreshold;
        this.hotelThreshold = hotelThreshold;
    }

    public void detect(TravelWatch watch, TravelSearchResult result) {
        boolean packageDrop = isDrop(priceHistory.findLowestPackagePrice(watch.getId()), result.getPackagePrice(), packageThreshold);
        boolean flightDrop = isDrop(priceHistory.findLowestFlightPrice(watch.getId()), result.getFlightPrice(), flightThreshold);
        boolean hotelDrop = isDrop(priceHistory.findLowestHotelPrice(watch.getId()), result.getHotelPrice(), hotelThreshold);
        if (packageDrop || flightDrop || hotelDrop) {
            BigDecimal previous = priceHistory.findLowestPackagePrice(watch.getId()).orElse(result.getPackagePrice());
            notifications.priceDrop(watch, result, previous.subtract(result.getPackagePrice()).max(BigDecimal.ZERO));
        }
        if (!result.getDestination().equalsIgnoreCase(watch.getDestination()) && result.getDealScore() >= 85) {
            notifications.strongAlternative(watch, result);
        }
    }

    private boolean isDrop(Optional<BigDecimal> previousBest, BigDecimal current, BigDecimal threshold) {
        return previousBest
            .filter(previous -> previous.compareTo(BigDecimal.ZERO) > 0)
            .map(previous -> current.compareTo(previous.multiply(BigDecimal.ONE.subtract(threshold))) <= 0)
            .orElse(false);
    }
}
