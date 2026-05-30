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
        List<String> destinations = new ArrayList<>();
        destinations.add(watch.getDestination());
        destinations.addAll(NEARBY_DESTINATIONS.getOrDefault(normalize(watch.getDestination()), List.of()));

        List<String> departureAirports = AIRPORTS.getOrDefault(normalize(watch.getDepartureLocation()), List.of(watch.getDepartureLocation()));
        Map<String, SearchCriteria> unique = new LinkedHashMap<>();
        for (String destination : destinations) {
            List<String> arrivalAirports = AIRPORTS.getOrDefault(normalize(destination), List.of(destination));
            for (int shift : dateShifts(watch.getFlexibilityDays())) {
                LocalDate shiftedStart = watch.getStartDate().plusDays(shift);
                LocalDate shiftedEnd = watch.getEndDate().plusDays(shift);
                addStayVariant(unique, watch, destination, departureAirports, arrivalAirports, shiftedStart, shiftedEnd, shift == 0 && destination.equals(watch.getDestination()));
                addStayVariant(unique, watch, destination, departureAirports, arrivalAirports, shiftedStart, shiftedEnd.minusDays(1), false);
                addStayVariant(unique, watch, destination, departureAirports, arrivalAirports, shiftedStart, shiftedEnd.plusDays(1), false);
            }
        }
        return unique.values().stream().limit(1200).toList();
    }

    private void addStayVariant(Map<String, SearchCriteria> unique, TravelWatch watch, String destination,
                                List<String> departureAirports, List<String> arrivalAirports,
                                LocalDate start, LocalDate end, boolean exact) {
        if (!end.isAfter(start)) {
            return;
        }
        for (String departureAirport : departureAirports) {
            for (String arrivalAirport : arrivalAirports) {
                SearchCriteria criteria = new SearchCriteria(watch.getId(), watch.getDepartureLocation(), destination,
                    departureAirport, arrivalAirport, start, end, watch.getTravellers(), watch.getTripType(),
                    watch.getPreferredHotelRating(), exact);
                unique.put(criteriaKey(criteria), criteria);
            }
        }
    }

    private List<Integer> dateShifts(int flexibilityDays) {
        List<Integer> shifts = new ArrayList<>();
        shifts.add(0);
        for (int i = 1; i <= flexibilityDays; i++) {
            shifts.add(-i);
            shifts.add(i);
        }
        return shifts;
    }

    private String criteriaKey(SearchCriteria criteria) {
        return criteria.destination() + criteria.departureAirport() + criteria.arrivalAirport() + criteria.startDate() + criteria.endDate();
    }

    private String normalize(String value) {
        return value == null ? "" : value.toLowerCase().trim();
    }
}
