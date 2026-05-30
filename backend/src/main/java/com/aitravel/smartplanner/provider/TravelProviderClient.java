package com.aitravel.smartplanner.provider;

import com.aitravel.smartplanner.search.SearchCriteria;
import java.util.List;

public interface TravelProviderClient {
    List<ProviderOffer> searchFlights(SearchCriteria criteria);
    List<ProviderOffer> searchHotels(SearchCriteria criteria);
    List<ProviderOffer> searchPackages(SearchCriteria criteria);

    default List<ProviderSignal> searchSignals(SearchCriteria criteria) {
        return List.of();
    }
}
