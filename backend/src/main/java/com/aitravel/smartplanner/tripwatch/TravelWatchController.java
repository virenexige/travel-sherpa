package com.aitravel.smartplanner.tripwatch;

import com.aitravel.smartplanner.pricing.PriceHistoryRepository;
import com.aitravel.smartplanner.pricing.dto.PriceHistoryResponse;
import com.aitravel.smartplanner.recommendation.RecommendationRepository;
import com.aitravel.smartplanner.recommendation.dto.RecommendationResponse;
import com.aitravel.smartplanner.search.TravelSearchResultRepository;
import com.aitravel.smartplanner.search.TravelSearchService;
import com.aitravel.smartplanner.search.dto.SearchResultResponse;
import com.aitravel.smartplanner.security.AuthenticatedUser;
import com.aitravel.smartplanner.tripwatch.dto.TravelWatchRequest;
import com.aitravel.smartplanner.tripwatch.dto.TravelWatchResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/travel-watches")
public class TravelWatchController {
    private final TravelWatchService watches;
    private final TravelSearchService searches;
    private final TravelSearchResultRepository results;
    private final PriceHistoryRepository priceHistory;
    private final RecommendationRepository recommendations;

    public TravelWatchController(TravelWatchService watches, TravelSearchService searches,
                                 TravelSearchResultRepository results, PriceHistoryRepository priceHistory,
                                 RecommendationRepository recommendations) {
        this.watches = watches;
        this.searches = searches;
        this.results = results;
        this.priceHistory = priceHistory;
        this.recommendations = recommendations;
    }

    @PostMapping
    TravelWatchResponse create(@AuthenticationPrincipal AuthenticatedUser user, @Valid @RequestBody TravelWatchRequest request) {
        return TravelWatchMapper.toResponse(watches.create(user.email(), user.name(), request));
    }

    @GetMapping
    List<TravelWatchResponse> list(@AuthenticationPrincipal AuthenticatedUser user) {
        return watches.list(user.email()).stream().map(TravelWatchMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    TravelWatchResponse get(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable UUID id) {
        return TravelWatchMapper.toResponse(watches.get(user.email(), id));
    }

    @PutMapping("/{id}")
    TravelWatchResponse update(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable UUID id,
                               @Valid @RequestBody TravelWatchRequest request) {
        return TravelWatchMapper.toResponse(watches.update(user.email(), id, request));
    }

    @PatchMapping("/{id}/pause")
    TravelWatchResponse pause(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable UUID id) {
        return TravelWatchMapper.toResponse(watches.pause(user.email(), id));
    }

    @PatchMapping("/{id}/resume")
    TravelWatchResponse resume(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable UUID id) {
        return TravelWatchMapper.toResponse(watches.resume(user.email(), id));
    }

    @DeleteMapping("/{id}")
    void delete(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable UUID id) {
        watches.delete(user.email(), id);
    }

    @PostMapping("/{id}/search-now")
    List<SearchResultResponse> searchNow(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable UUID id) {
        return searches.runSearch(watches.get(user.email(), id)).stream().map(this::toResultResponse).toList();
    }

    @GetMapping("/{id}/results")
    List<SearchResultResponse> results(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable UUID id) {
        watches.get(user.email(), id);
        return results.findTop50ByTravelWatch_IdOrderBySearchedAtDescDealScoreDesc(id).stream()
            .map(this::toResultResponse)
            .toList();
    }

    @GetMapping("/{id}/price-history")
    List<PriceHistoryResponse> priceHistory(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable UUID id) {
        watches.get(user.email(), id);
        return priceHistory.findByTravelWatch_IdOrderBySearchedAtAsc(id).stream()
            .map(item -> new PriceHistoryResponse(item.getId(), item.getProviderName(), item.getPackagePrice(),
                item.getFlightPrice(), item.getHotelPrice(), item.getCurrency(), item.getSearchedAt()))
            .toList();
    }

    @GetMapping("/{id}/recommendations")
    List<RecommendationResponse> recommendations(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable UUID id) {
        watches.get(user.email(), id);
        return recommendations.findTop10ByTravelWatch_IdOrderByCreatedAtDesc(id).stream()
            .map(item -> new RecommendationResponse(item.getId(), item.getTitle(), item.getExplanation(),
                item.getRecommendationType(), item.getConfidenceScore(), item.getEstimatedSaving(), item.getCreatedAt()))
            .toList();
    }

    private SearchResultResponse toResultResponse(com.aitravel.smartplanner.search.TravelSearchResult item) {
        return new SearchResultResponse(item.getId(), item.getProviderName(), item.getDestination(),
            item.getDepartureAirport(), item.getArrivalAirport(), item.getStartDate(), item.getEndDate(),
            item.getFlightPrice(), item.getHotelPrice(), item.getPackagePrice(), item.getCurrency(),
            item.getDealScore(), item.getResultUrl(), item.getSearchedAt());
    }
}
