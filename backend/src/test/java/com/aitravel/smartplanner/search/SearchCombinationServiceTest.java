package com.aitravel.smartplanner.search;

import static org.assertj.core.api.Assertions.assertThat;

import com.aitravel.smartplanner.tripwatch.TravelWatch;
import com.aitravel.smartplanner.tripwatch.TravelWatchStatus;
import com.aitravel.smartplanner.user.AppUser;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class SearchCombinationServiceTest {
    private final SearchCombinationService service = new SearchCombinationService();

    @Test
    void createsFlexibleNearbyDestinationAndAirportCombinations() {
        AppUser user = new AppUser(UUID.randomUUID(), "Test User", "test@example.com", Instant.now());
        TravelWatch watch = new TravelWatch(UUID.randomUUID(), user, "London", "Zurich",
            LocalDate.of(2026, 8, 10), LocalDate.of(2026, 8, 17), 3, 3,
            new BigDecimal("3000"), "Family + Nature", 4, TravelWatchStatus.ACTIVE, Instant.now(), Instant.now());

        var combinations = service.generate(watch);

        assertThat(combinations).isNotEmpty();
        assertThat(combinations).anyMatch(criteria -> criteria.destination().equals("Lucerne"));
        assertThat(combinations).anyMatch(criteria -> criteria.departureAirport().equals("LGW"));
        assertThat(combinations).anyMatch(criteria -> criteria.startDate().equals(LocalDate.of(2026, 8, 10)));
        assertThat(combinations).allMatch(criteria -> ChronoUnit.DAYS.between(criteria.startDate(), criteria.endDate()) == 7);
    }
}
