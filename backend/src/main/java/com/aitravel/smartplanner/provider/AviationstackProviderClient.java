package com.aitravel.smartplanner.provider;

import com.aitravel.smartplanner.search.SearchCriteria;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.Instant;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@ConditionalOnProperty(prefix = "app.providers.aviationstack", name = "enabled", havingValue = "true")
public class AviationstackProviderClient implements TravelProviderClient {
    private static final String PROVIDER = "Aviationstack";
    private final String apiKey;
    private final String baseUrl;
    private final int monthlyLimit;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final Map<String, CachedSignal> cache = new ConcurrentHashMap<>();
    private final AtomicInteger callsThisMonth = new AtomicInteger();
    private YearMonth currentMonth = YearMonth.now();

    public AviationstackProviderClient(
        @Value("${app.providers.aviationstack.api-key:}") String apiKey,
        @Value("${app.providers.aviationstack.base-url:https://api.aviationstack.com/v1}") String baseUrl,
        @Value("${app.providers.aviationstack.monthly-limit:80}") int monthlyLimit,
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
        return List.of();
    }

    @Override
    public List<ProviderOffer> searchHotels(SearchCriteria criteria) {
        return List.of();
    }

    @Override
    public List<ProviderOffer> searchPackages(SearchCriteria criteria) {
        return List.of();
    }

    @Override
    public List<ProviderSignal> searchSignals(SearchCriteria criteria) {
        if (!criteria.exactMatch()) {
            return List.of();
        }
        if (apiKey == null || apiKey.isBlank()) {
            return List.of(new ProviderSignal(PROVIDER, "SKIPPED", null,
                "Aviationstack is enabled but no API key is configured. Set AVIATIONSTACK_API_KEY to query live flight schedule data."));
        }
        String cacheKey = criteria.departureAirport() + ":" + criteria.arrivalAirport();
        CachedSignal cached = cache.get(cacheKey);
        if (cached != null && cached.createdAt().plus(Duration.ofHours(24)).isAfter(Instant.now())) {
            return List.of(cached.signal());
        }
        resetMonthlyCounterIfNeeded();
        if (callsThisMonth.get() >= monthlyLimit) {
            return List.of(new ProviderSignal(PROVIDER, "SKIPPED", null,
                "Aviationstack monthly safety cap reached. Skipped to preserve the 100/month API allowance."));
        }
        callsThisMonth.incrementAndGet();
        ProviderSignal signal = fetchFlightSignal(criteria);
        cache.put(cacheKey, new CachedSignal(signal, Instant.now()));
        return List.of(signal);
    }

    private ProviderSignal fetchFlightSignal(SearchCriteria criteria) {
        try {
            String body = restClient.get()
                .uri(baseUrl + "/flights?access_key={key}&dep_iata={dep}&arr_iata={arr}&limit=5",
                    apiKey, criteria.departureAirport(), criteria.arrivalAirport())
                .retrieve()
                .body(String.class);
            JsonNode root = objectMapper.readTree(body);
            if (root.has("error")) {
                String info = Optional.ofNullable(root.path("error").path("info").asText(null))
                    .orElse("Aviationstack returned an error.");
                return new ProviderSignal(PROVIDER, "ERROR", null, info);
            }
            JsonNode data = root.path("data");
            if (!data.isArray() || data.isEmpty()) {
                return new ProviderSignal(PROVIDER, "NO_FLIGHTS", null,
                    "Aviationstack returned no current schedule records for " + criteria.departureAirport()
                        + " to " + criteria.arrivalAirport() + ". It does not provide ticket prices.");
            }
            StringBuilder message = new StringBuilder("Aviationstack schedule sample for ")
                .append(criteria.departureAirport()).append(" to ").append(criteria.arrivalAirport())
                .append(": ");
            int count = Math.min(3, data.size());
            for (int i = 0; i < count; i++) {
                JsonNode flight = data.get(i);
                if (i > 0) {
                    message.append("; ");
                }
                message.append(flight.path("airline").path("name").asText("Unknown airline"))
                    .append(" ")
                    .append(flight.path("flight").path("iata").asText("flight"))
                    .append(" status ")
                    .append(flight.path("flight_status").asText("unknown"));
            }
            message.append(". Aviationstack provides schedule/status data, not fare pricing.");
            return new ProviderSignal(PROVIDER, "COMPLETED", null, message.toString());
        } catch (Exception ex) {
            return new ProviderSignal(PROVIDER, "ERROR", null,
                "Aviationstack request failed: " + ex.getMessage());
        }
    }

    private synchronized void resetMonthlyCounterIfNeeded() {
        YearMonth now = YearMonth.now();
        if (!now.equals(currentMonth)) {
            currentMonth = now;
            callsThisMonth.set(0);
            cache.clear();
        }
    }

    private record CachedSignal(ProviderSignal signal, Instant createdAt) {
    }
}
