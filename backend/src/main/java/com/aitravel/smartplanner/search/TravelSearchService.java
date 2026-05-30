package com.aitravel.smartplanner.search;

import com.aitravel.smartplanner.ai.AIRecommendationService;
import com.aitravel.smartplanner.pricing.DealScoreService;
import com.aitravel.smartplanner.pricing.PriceDropDetector;
import com.aitravel.smartplanner.pricing.PriceHistory;
import com.aitravel.smartplanner.pricing.PriceHistoryRepository;
import com.aitravel.smartplanner.provider.ProviderOffer;
import com.aitravel.smartplanner.provider.TravelProviderClient;
import com.aitravel.smartplanner.recommendation.RecommendationRepository;
import com.aitravel.smartplanner.tripwatch.TravelWatch;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TravelSearchService {
    private final SearchCombinationService combinations;
    private final List<TravelProviderClient> providers;
    private final DealScoreService dealScores;
    private final TravelSearchResultRepository results;
    private final PriceHistoryRepository priceHistory;
    private final AIRecommendationService aiRecommendations;
    private final RecommendationRepository recommendations;
    private final PriceDropDetector priceDrops;

    public TravelSearchService(SearchCombinationService combinations, List<TravelProviderClient> providers,
                               DealScoreService dealScores, TravelSearchResultRepository results,
                               PriceHistoryRepository priceHistory, AIRecommendationService aiRecommendations,
                               RecommendationRepository recommendations, PriceDropDetector priceDrops) {
        this.combinations = combinations;
        this.providers = providers;
        this.dealScores = dealScores;
        this.results = results;
        this.priceHistory = priceHistory;
        this.aiRecommendations = aiRecommendations;
        this.recommendations = recommendations;
        this.priceDrops = priceDrops;
    }

    @Transactional
    public List<TravelSearchResult> runSearch(TravelWatch watch) {
        List<TravelSearchResult> saved = new ArrayList<>();
        for (SearchCriteria criteria : combinations.generate(watch)) {
            for (TravelProviderClient provider : providers) {
                for (ProviderOffer offer : provider.searchPackages(criteria)) {
                    int score = dealScores.score(watch, criteria, offer);
                    Instant searchedAt = Instant.now();
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
}
