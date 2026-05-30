package com.aitravel.smartplanner.provider;

import com.aitravel.smartplanner.search.SearchCriteria;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@ConditionalOnProperty(prefix = "app.providers.serpapi", name = "enabled", havingValue = "true")
public class SerpApiGoogleFlightsProviderClient implements TravelProviderClient {
    private static final String PROVIDER = "SerpApi Google Flights";
    private final String apiKey;
    private final String baseUrl;
    private final int monthlyLimit;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final Map<String, CachedOffers> cache = new ConcurrentHashMap<>();
    private final AtomicInteger callsThisMonth = new AtomicInteger();
    private YearMonth currentMonth = YearMonth.now();

    public SerpApiGoogleFlightsProviderClient(
        @Value("${app.providers.serpapi.api-key:}") String apiKey,
        @Value("${app.providers.serpapi.base-url:https://serpapi.com/search.json}") String baseUrl,
        @Value("${app.providers.serpapi.monthly-limit:200}") int monthlyLimit,
        RestClient.Builder restClientBuilder,
        ObjectMapper objectMapper) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.monthlyLimit = monthlyLimit;
        this.restClient = restClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    @Override
    public List<ProviderOffer> searchFlights(SearchCriteria criteria) {
        if (!criteria.exactMatch()) {
            return List.of();
        }
        if (apiKey == null || apiKey.isBlank()) {
            return List.of();
        }
        String cacheKey = criteria.departureAirport() + ":" + criteria.arrivalAirport() + ":"
            + criteria.startDate() + ":" + criteria.endDate() + ":" + criteria.cabinClass();
        CachedOffers cached = cache.get(cacheKey);
        if (cached != null && cached.createdAt().plus(Duration.ofHours(24)).isAfter(Instant.now())) {
            return cached.offers();
        }
        resetMonthlyCounterIfNeeded();
        if (callsThisMonth.get() >= monthlyLimit) {
            return List.of();
        }
        callsThisMonth.incrementAndGet();
        List<ProviderOffer> offers = fetchFlights(criteria);
        cache.put(cacheKey, new CachedOffers(offers, Instant.now()));
        return offers;
    }

    @Override
    public List<ProviderOffer> searchHotels(SearchCriteria criteria) {
        return List.of();
    }

    @Override
    public List<ProviderOffer> searchPackages(SearchCriteria criteria) {
        return searchFlights(criteria);
    }

    @Override
    public List<ProviderSignal> searchSignals(SearchCriteria criteria) {
        if (!criteria.exactMatch()) {
            return List.of();
        }
        if (apiKey == null || apiKey.isBlank()) {
            return List.of(new ProviderSignal(PROVIDER, "SKIPPED", "GBP",
                "SerpApi is enabled but no API key is configured. Set SERPAPI_API_KEY to query Google Flights results through SerpApi."));
        }
        if (callsThisMonth.get() >= monthlyLimit) {
            return List.of(new ProviderSignal(PROVIDER, "SKIPPED", "GBP",
                "SerpApi monthly safety cap reached. Skipped to preserve the 250/month search allowance."));
        }
        return List.of(new ProviderSignal(PROVIDER, "CONFIGURED", "GBP",
            "SerpApi Google Flights is configured for exact-route fare lookup only. Flexible alternatives remain limited to mock estimates to protect monthly quota."));
    }

    private List<ProviderOffer> fetchFlights(SearchCriteria criteria) {
        try {
            String body = restClient.get()
                .uri(UriComponentsBuilder.fromHttpUrl(baseUrl)
                    .queryParam("engine", "google_flights")
                    .queryParam("departure_id", criteria.departureAirport())
                    .queryParam("arrival_id", criteria.arrivalAirport())
                    .queryParam("outbound_date", criteria.startDate())
                    .queryParam("return_date", criteria.endDate())
                    .queryParam("currency", "GBP")
                    .queryParam("hl", "en")
                    .queryParam("gl", "uk")
                    .queryParam("api_key", apiKey)
                    .queryParamIfPresent("travel_class", travelClass(criteria.cabinClass()))
                    .build(true)
                    .toUri())
                .retrieve()
                .body(String.class);
            JsonNode root = objectMapper.readTree(body);
            if (root.has("error")) {
                return List.of();
            }
            List<ProviderOffer> offers = new ArrayList<>();
            addOffers(criteria, root.path("best_flights"), offers);
            addOffers(criteria, root.path("other_flights"), offers);
            return offers.stream().limit(5).toList();
        } catch (Exception ex) {
            return List.of();
        }
    }

    private void addOffers(SearchCriteria criteria, JsonNode flights, List<ProviderOffer> offers) {
        if (!flights.isArray()) {
            return;
        }
        for (JsonNode flight : flights) {
            if (!flight.has("price")) {
                continue;
            }
            String priceText = flight.path("price").asText().replaceAll("[^0-9.]", "");
            if (priceText.isBlank()) {
                continue;
            }
            BigDecimal price = new BigDecimal(priceText);
            offers.add(new ProviderOffer(PROVIDER, criteria.destination(), criteria.departureAirport(),
                criteria.arrivalAirport(), price, BigDecimal.ZERO, price, "GBP",
                flight.path("booking_token").asText("https://www.google.com/travel/flights")));
        }
    }

    private Optional<Integer> travelClass(String cabinClass) {
        return switch (cabinClass == null ? "ALL" : cabinClass) {
            case "PREMIUM_ECONOMY" -> Optional.of(2);
            case "BUSINESS" -> Optional.of(3);
            case "FIRST" -> Optional.of(4);
            case "ECONOMY" -> Optional.of(1);
            default -> Optional.empty();
        };
    }

    private synchronized void resetMonthlyCounterIfNeeded() {
        YearMonth now = YearMonth.now();
        if (!now.equals(currentMonth)) {
            currentMonth = now;
            callsThisMonth.set(0);
            cache.clear();
        }
    }

    private record CachedOffers(List<ProviderOffer> offers, Instant createdAt) {
    }
}
