package com.aitravel.smartplanner.notification;

import com.aitravel.smartplanner.search.TravelSearchResult;
import com.aitravel.smartplanner.tripwatch.TravelWatch;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    public void priceDrop(TravelWatch watch, TravelSearchResult result, BigDecimal saving) {
        log.info("Price drop alert for {}: {} to {} dropped by {} {}. Best option: {} for {}",
            watch.getUser().getEmail(), watch.getDepartureLocation(), watch.getDestination(), result.getCurrency(),
            saving, result.getDestination(), result.getPackagePrice());
    }

    public void strongAlternative(TravelWatch watch, TravelSearchResult result) {
        log.info("Strong alternative deal for {}: {} is available for {} {}",
            watch.getUser().getEmail(), result.getDestination(), result.getCurrency(), result.getPackagePrice());
    }
}
