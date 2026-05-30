package com.aitravel.smartplanner.search;

import com.aitravel.smartplanner.tripwatch.TravelWatch;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class SearchCombinationService {
    private static final Map<String, List<String>> NEARBY_DESTINATIONS = Map.of(
        "zurich", List.of("Lucerne", "Interlaken", "Basel", "Geneva", "Milan", "Lake Como", "Innsbruck"),
        "paris", List.of("Lille", "Brussels", "Reims", "Lyon", "Normandy"),
        "barcelona", List.of("Girona", "Valencia", "Mallorca", "Tarragona")
    );
    private static final Map<String, List<String>> AIRPORTS = Map.of(
        "london", List.of("LHR", "LGW", "STN", "LTN", "LCY"),
        "zurich", List.of("ZRH", "BSL", "GVA", "MXP"),
        "basel", List.of("BSL", "ZRH", "MLH"),
        "geneva", List.of("GVA", "ZRH", "LYS"),
        "milan", List.of("MXP", "LIN", "BGY")
    );

    public List<SearchCriteria> generate(TravelWatch watch) {
        LocalDate baseStart = watch.getStartDate();
        LocalDate baseEnd = watch.getEndDate();
        if (watch.isBucketList() && watch.getEarliestStartDate() != null && watch.getLatestEndDate() != null) {
            baseStart = watch.getEarliestStartDate();
            baseEnd = watch.getLatestEndDate();
        }
        List<DateRange> selectedRanges = selectedRanges(watch, baseStart, baseEnd);
        List<String> destinations = new ArrayList<>();
        destinations.add(watch.getDestination());
        destinations.addAll(NEARBY_DESTINATIONS.getOrDefault(normalize(watch.getDestination()), List.of()));

        List<String> departureAirports = AIRPORTS.getOrDefault(normalize(watch.getDepartureLocation()), List.of(watch.getDepartureLocation()));
        Map<String, SearchCriteria> unique = new LinkedHashMap<>();
        for (DateRange range : selectedRanges) {
            for (int duration : durationOptions(watch)) {
                LocalDate latestStart = range.endDate().minusDays(duration);
                if (latestStart.isBefore(range.startDate())) {
                    continue;
                }
                for (LocalDate start = range.startDate(); !start.isAfter(latestStart); start = start.plusDays(1)) {
                    addCombinationsForWindow(watch, destinations, departureAirports, unique, range, start, duration);
                }
            }
        }
        return unique.values().stream().limit(320).toList();
    }

    private void addCombinationsForWindow(TravelWatch watch, List<String> destinations, List<String> departureAirports,
                                          Map<String, SearchCriteria> unique, DateRange range,
                                          LocalDate start, int duration) {
        for (String destination : destinations) {
            List<String> arrivalAirports = AIRPORTS.getOrDefault(normalize(destination), List.of(destination));
            LocalDate end = start.plusDays(duration);
            if (watch.isBucketList() && watch.getLatestEndDate() != null && end.isAfter(watch.getLatestEndDate())) {
                continue;
            }
            boolean exact = duration == watch.getTripDurationDays() && start.equals(range.startDate())
                && range.primary() && destination.equals(watch.getDestination());
            addStayVariant(unique, watch, destination, departureAirports, arrivalAirports, start, end, exact);
        }
    }

    private List<Integer> durationOptions(TravelWatch watch) {
        List<Integer> durations = new ArrayList<>();
        int baseDuration = Math.max(1, watch.getTripDurationDays());
        durations.add(baseDuration);
        for (int i = 1; i <= watch.getFinishDaysEarly(); i++) {
            if (baseDuration - i >= 1) {
                durations.add(baseDuration - i);
            }
        }
        int maxLonger = Math.max(watch.getFinishDaysLate(), watch.getDurationIncreaseDays());
        for (int i = 1; i <= maxLonger; i++) {
            durations.add(baseDuration + i);
        }
        return durations.stream().distinct().toList();
    }

    private void addStayVariant(Map<String, SearchCriteria> unique, TravelWatch watch, String destination,
                                List<String> departureAirports, List<String> arrivalAirports,
                                LocalDate start, LocalDate end, boolean exact) {
        if (!end.isAfter(start)) {
            return;
        }
        for (String departureAirport : departureAirports) {
            for (String arrivalAirport : arrivalAirports) {
                for (String productType : splitOptions(watch.getTravelProductType(), "PACKAGE")) {
                    for (String cabinClass : splitOptions(watch.getCabinClass(), "ECONOMY")) {
                        SearchCriteria criteria = new SearchCriteria(watch.getId(), watch.getDepartureLocation(), destination,
                            departureAirport, arrivalAirport, start, end, watch.getTravellers(), watch.getTripType(),
                            watch.getPreferredHotelRating(), productType, cabinClass, exact);
                        unique.put(criteriaKey(criteria), criteria);
                    }
                }
            }
        }
    }

    private List<DateRange> selectedRanges(TravelWatch watch, LocalDate baseStart, LocalDate baseEnd) {
        List<DateRange> ranges = new ArrayList<>();
        ranges.add(new DateRange(baseStart, baseEnd, true));
        if (watch.getRange2StartDate() != null && watch.getRange2EndDate() != null) {
            ranges.add(new DateRange(watch.getRange2StartDate(), watch.getRange2EndDate(), false));
        }
        if (watch.getRange3StartDate() != null && watch.getRange3EndDate() != null) {
            ranges.add(new DateRange(watch.getRange3StartDate(), watch.getRange3EndDate(), false));
        }
        return ranges;
    }

    private record DateRange(LocalDate startDate, LocalDate endDate, boolean primary) {
    }

    private String criteriaKey(SearchCriteria criteria) {
        return criteria.destination() + criteria.departureAirport() + criteria.arrivalAirport() + criteria.startDate()
            + criteria.endDate() + criteria.travelProductType() + criteria.cabinClass();
    }

    private List<String> splitOptions(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return List.of(fallback);
        }
        return java.util.Arrays.stream(value.split(","))
            .map(String::trim)
            .filter(option -> !option.isBlank())
            .distinct()
            .toList();
    }

    private String normalize(String value) {
        return value == null ? "" : value.toLowerCase().trim();
    }
}
