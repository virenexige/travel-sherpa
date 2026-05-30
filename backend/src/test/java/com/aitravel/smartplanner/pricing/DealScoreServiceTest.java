package com.aitravel.smartplanner.pricing;

import static org.assertj.core.api.Assertions.assertThat;

import com.aitravel.smartplanner.provider.ProviderOffer;
import com.aitravel.smartplanner.search.SearchCriteria;
import com.aitravel.smartplanner.tripwatch.TravelWatch;
import com.aitravel.smartplanner.tripwatch.TravelWatchStatus;
import com.aitravel.smartplanner.user.AppUser;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DealScoreServiceTest {
    private final DealScoreService service = new DealScoreService();

    @Test
    void givesHigherScoreToUnderBudgetExactMatch() {
        AppUser user = new AppUser(UUID.randomUUID(), "Test User", "test@example.com", Instant.now());
        TravelWatch watch = new TravelWatch(UUID.randomUUID(), user, "London", "Zurich",
            LocalDate.now().plusDays(20), LocalDate.now().plusDays(27), 3, 3,
            new BigDecimal("3000"), "Family + Nature", 4, TravelWatchStatus.ACTIVE, Instant.now(), Instant.now());
        SearchCriteria criteria = new SearchCriteria(watch.getId(), "London", "Zurich", "LHR", "ZRH",
            watch.getStartDate(), watch.getEndDate(), 3, watch.getTripType(), 4, "PACKAGE", "ALL", true);
        ProviderOffer offer = new ProviderOffer("Mock", "Zurich", "LHR", "ZRH", new BigDecimal("900"),
            new BigDecimal("1200"), new BigDecimal("2100"), "GBP", "https://example.com");

        int score = service.score(watch, criteria, offer);

        assertThat(score).isGreaterThanOrEqualTo(90);
    }
}
