package com.aitravel.smartplanner.provider;

import com.aitravel.smartplanner.search.SearchCriteria;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app.providers.booking", name = "enabled", havingValue = "true")
public class BookingProviderClient implements TravelProviderClient {
    @Override
    public List<ProviderOffer> searchFlights(SearchCriteria criteria) {
        return List.of();
    }

    @Override
    public List<ProviderOffer> searchHotels(SearchCriteria criteria) {
        throw new UnsupportedOperationException("Configure Booking API credentials and terms before enabling this provider.");
    }

    @Override
    public List<ProviderOffer> searchPackages(SearchCriteria criteria) {
        return List.of();
    }
}
