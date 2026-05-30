package com.aitravel.smartplanner.provider;

import com.aitravel.smartplanner.search.SearchCriteria;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MockTravelProviderClient implements TravelProviderClient {
    private static final String PROVIDER = "MockTravel";

    @Override
    public List<ProviderOffer> searchFlights(SearchCriteria criteria) {
        ProviderOffer offer = createOffer(criteria, BigDecimal.ONE);
        return List.of(new ProviderOffer(PROVIDER, criteria.destination(), criteria.departureAirport(), criteria.arrivalAirport(),
            offer.flightPrice(), BigDecimal.ZERO, offer.flightPrice(), "GBP", offer.resultUrl()));
    }

    @Override
    public List<ProviderOffer> searchHotels(SearchCriteria criteria) {
        ProviderOffer offer = createOffer(criteria, BigDecimal.ONE);
        return List.of(new ProviderOffer(PROVIDER, criteria.destination(), criteria.departureAirport(), criteria.arrivalAirport(),
            BigDecimal.ZERO, offer.hotelPrice(), offer.hotelPrice(), "GBP", offer.resultUrl()));
    }

    @Override
    public List<ProviderOffer> searchPackages(SearchCriteria criteria) {
        return List.of(createOffer(criteria, BigDecimal.ONE), createOffer(criteria, new BigDecimal("0.94")));
    }

    private ProviderOffer createOffer(SearchCriteria criteria, BigDecimal multiplier) {
        long nights = Math.max(1, ChronoUnit.DAYS.between(criteria.startDate(), criteria.endDate()));
        int seed = Math.abs((criteria.destination() + criteria.departureAirport() + criteria.arrivalAirport()
            + criteria.startDate()).hashCode());
        BigDecimal destinationFactor = BigDecimal.valueOf(80 + seed % 95);
        BigDecimal travellerFactor = BigDecimal.valueOf(criteria.travellers());
        BigDecimal flight = destinationFactor.multiply(travellerFactor)
            .add(BigDecimal.valueOf((seed % 7) * 22L))
            .multiply(multiplier);
        BigDecimal hotelNight = BigDecimal.valueOf(95 + seed % 160)
            .add(BigDecimal.valueOf((criteria.preferredHotelRating() == null ? 3 : criteria.preferredHotelRating()) * 18L));
        BigDecimal hotel = hotelNight.multiply(BigDecimal.valueOf(nights)).multiply(multiplier);
        BigDecimal packagePrice = flight.add(hotel).multiply(new BigDecimal("0.93"));
        if (!criteria.exactMatch()) {
            packagePrice = packagePrice.multiply(BigDecimal.valueOf(0.82 + (seed % 18) / 100.0));
        }
        return new ProviderOffer(
            PROVIDER,
            criteria.destination(),
            criteria.departureAirport(),
            criteria.arrivalAirport(),
            money(flight),
            money(hotel),
            money(packagePrice),
            "GBP",
            "https://example.com/mock-deal/" + criteria.travelWatchId() + "/" + seed
        );
    }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
