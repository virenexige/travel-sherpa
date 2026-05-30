package com.aitravel.smartplanner.search;

import com.aitravel.smartplanner.ai.AIRecommendationService;
import com.aitravel.smartplanner.pricing.DealScoreService;
import com.aitravel.smartplanner.pricing.PriceDropDetector;
import com.aitravel.smartplanner.pricing.PriceHistory;
import com.aitravel.smartplanner.pricing.PriceHistoryRepository;
import com.aitravel.smartplanner.provider.ProviderOffer;
import com.aitravel.smartplanner.provider.ProviderSignal;
import com.aitravel.smartplanner.provider.TravelProviderClient;
import com.aitravel.smartplanner.recommendation.RecommendationRepository;
import com.aitravel.smartplanner.tripwatch.TravelWatch;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TravelSearchService {
    private final SearchCombinationService combinations;
    private final List<TravelProviderClient> providers;
    private final DealScoreService dealScores;
    private final TravelSearchResultRepository results;
    private final SearchActivityLogRepository searchLogs;
    private final PriceHistoryRepository priceHistory;
    private final AIRecommendationService aiRecommendations;
    private final RecommendationRepository recommendations;
    private final PriceDropDetector priceDrops;

    public TravelSearchService(SearchCombinationService combinations, List<TravelProviderClient> providers,
                               DealScoreService dealScores, TravelSearchResultRepository results,
                               SearchActivityLogRepository searchLogs,
                               PriceHistoryRepository priceHistory, AIRecommendationService aiRecommendations,
                               RecommendationRepository recommendations, PriceDropDetector priceDrops) {
        this.combinations = combinations;
        this.providers = providers;
        this.dealScores = dealScores;
        this.results = results;
        this.searchLogs = searchLogs;
        this.priceHistory = priceHistory;
        this.aiRecommendations = aiRecommendations;
        this.recommendations = recommendations;
        this.priceDrops = priceDrops;
    }

    @Transactional
    public List<TravelSearchResult> runSearch(TravelWatch watch) {
        List<TravelSearchResult> saved = new ArrayList<>();
        Set<String> signalProvidersCalled = new HashSet<>();
        Set<String> fareProvidersCalled = new HashSet<>();
        for (SearchCriteria criteria : combinations.generate(watch)) {
            for (TravelProviderClient provider : providers) {
                Instant searchedAt = Instant.now();
                List<ProviderOffer> offers = shouldQueryFares(provider, criteria, fareProvidersCalled)
                    ? searchOffers(provider, criteria)
                    : List.of();
                if (!offers.isEmpty()) {
                    fareProvidersCalled.add(provider.getClass().getName());
                }
                List<ProviderSignal> signals = shouldQuerySignals(provider, criteria, signalProvidersCalled)
                    ? provider.searchSignals(criteria)
                    : List.of();
                if (!signals.isEmpty()) {
                    signalProvidersCalled.add(provider.getClass().getName());
                }
                for (ProviderSignal signal : signals) {
                    searchLogs.save(new SearchActivityLog(UUID.randomUUID(), watch, signal.providerName(),
                        "PROVIDER_SIGNAL", criteria.departureLocation(), criteria.destination(), criteria.departureAirport(),
                        criteria.arrivalAirport(), criteria.startDate(), criteria.endDate(), signal.status(),
                        0, null, signal.currency(), signal.message(), searchedAt));
                }
                if (offers.isEmpty()) {
                    continue;
                }
                ProviderOffer cheapest = offers.stream()
                    .min(Comparator.comparing(ProviderOffer::packagePrice))
                    .orElse(null);
                searchLogs.save(new SearchActivityLog(UUID.randomUUID(), watch,
                    cheapest == null ? provider.getClass().getSimpleName() : cheapest.providerName(),
                    searchType(criteria, watch),
                    criteria.departureLocation(), criteria.destination(), criteria.departureAirport(),
                    criteria.arrivalAirport(), criteria.startDate(), criteria.endDate(),
                    "COMPLETED", offers.size(), cheapest == null ? null : cheapest.packagePrice(),
                    cheapest == null ? null : cheapest.currency(),
                    buildLogMessage(criteria, offers, cheapest),
                    searchedAt));
                for (ProviderOffer offer : offers) {
                    int score = dealScores.score(watch, criteria, offer);
                    TravelSearchResult result = new TravelSearchResult(UUID.randomUUID(), watch, offer.providerName(),
                        offer.destination(), offer.departureAirport(), offer.arrivalAirport(), criteria.startDate(),
                        criteria.endDate(), offer.flightPrice(), offer.hotelPrice(), offer.packagePrice(),
                        offer.currency(), score, offer.resultUrl(), searchedAt);
                    priceDrops.detect(watch, result);
                    saved.add(results.save(result));
                    priceHistory.save(new PriceHistory(UUID.randomUUID(), watch, offer.providerName(),
                        offer.packagePrice(), offer.flightPrice(), offer.hotelPrice(), offer.currency(), searchedAt));
                }
            }
        }
        List<TravelSearchResult> topResults = saved.stream()
            .sorted(Comparator.comparing(TravelSearchResult::getPackagePrice))
            .limit(20)
            .toList();
        recommendations.saveAll(aiRecommendations.generate(watch, topResults));
        return topResults;
    }

    private String buildLogMessage(SearchCriteria criteria, List<ProviderOffer> offers, ProviderOffer cheapest) {
        String sourceNote = "Queried permitted provider adapter. No website scraping was performed.";
        if (offers.isEmpty()) {
            return sourceNote + " No package offers returned for " + criteria.departureAirport() + " to "
                + criteria.arrivalAirport() + " on " + criteria.startDate() + " to " + criteria.endDate() + ".";
        }
        String product = productLabel(criteria);
        return sourceNote + " Returned " + offers.size() + " " + product + " offer(s). Cabin class: "
            + criteria.cabinClass() + ". Cheapest " + product + " was "
            + cheapest.currency() + " " + cheapest.packagePrice() + " for " + criteria.destination()
            + " from " + criteria.departureAirport() + " to " + criteria.arrivalAirport() + ".";
    }

    private boolean isFlightOnly(SearchCriteria criteria) {
        return "FLIGHT_ONLY".equals(criteria.travelProductType());
    }

    private boolean isHotelOnly(SearchCriteria criteria) {
        return "HOTEL_ONLY".equals(criteria.travelProductType());
    }

    private List<ProviderOffer> searchOffers(TravelProviderClient provider, SearchCriteria criteria) {
        if (isFlightOnly(criteria)) {
            return provider.searchFlights(criteria);
        }
        if (isHotelOnly(criteria)) {
            return provider.searchHotels(criteria);
        }
        return provider.searchPackages(criteria);
    }

    private String searchType(SearchCriteria criteria, TravelWatch watch) {
        String base = criteria.exactMatch() ? "EXACT_ORIGINAL" : "SMART_ALTERNATIVE";
        if (isFlightOnly(criteria)) {
            return "FLIGHT_ONLY_" + base;
        }
        if (isHotelOnly(criteria)) {
            return "HOTEL_ONLY_" + base;
        }
        return base;
    }

    private String productLabel(SearchCriteria criteria) {
        if (isFlightOnly(criteria)) {
            return "flight";
        }
        if (isHotelOnly(criteria)) {
            return "hotel";
        }
        return "package";
    }

    private boolean shouldQuerySignals(TravelProviderClient provider, SearchCriteria criteria, Set<String> signalProvidersCalled) {
        return criteria.exactMatch() && !signalProvidersCalled.contains(provider.getClass().getName());
    }

    private boolean shouldQueryFares(TravelProviderClient provider, SearchCriteria criteria, Set<String> fareProvidersCalled) {
        boolean cappedExternalProvider = provider.getClass().getSimpleName().contains("SerpApi");
        return !cappedExternalProvider || (criteria.exactMatch() && !fareProvidersCalled.contains(provider.getClass().getName()));
    }
}
