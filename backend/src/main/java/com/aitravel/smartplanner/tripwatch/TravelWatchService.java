package com.aitravel.smartplanner.tripwatch;

import com.aitravel.smartplanner.common.NotFoundException;
import com.aitravel.smartplanner.search.TravelSearchService;
import com.aitravel.smartplanner.tripwatch.dto.TravelWatchRequest;
import com.aitravel.smartplanner.user.AppUser;
import com.aitravel.smartplanner.user.UserService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TravelWatchService {
    private final TravelWatchRepository watches;
    private final UserService users;
    private final TravelSearchService searches;

    public TravelWatchService(TravelWatchRepository watches, UserService users, TravelSearchService searches) {
        this.watches = watches;
        this.users = users;
        this.searches = searches;
    }

    @Transactional
    public TravelWatch create(String email, String name, TravelWatchRequest request) {
        validateDates(request);
        AppUser user = users.findOrCreate(email, name);
        Instant now = Instant.now();
        TravelWatch watch = watches.save(new TravelWatch(UUID.randomUUID(), user, request.departureLocation(),
            request.destination(), request.startDate(), request.endDate(), request.travellers(),
            request.flexibilityDays(), request.maxBudget(), request.tripType(), request.preferredHotelRating(),
            TravelWatchStatus.ACTIVE, now, now));
        searches.runSearch(watch);
        return watch;
    }

    @Transactional(readOnly = true)
    public List<TravelWatch> list(String email) {
        return watches.findByUser_EmailOrderByCreatedAtDesc(email);
    }

    @Transactional(readOnly = true)
    public TravelWatch get(String email, UUID id) {
        return watches.findByIdAndUser_Email(id, email).orElseThrow(() -> new NotFoundException("Travel watch not found"));
    }

    @Transactional
    public TravelWatch update(String email, UUID id, TravelWatchRequest request) {
        validateDates(request);
        TravelWatch watch = get(email, id);
        watch.update(request.departureLocation(), request.destination(), request.startDate(), request.endDate(),
            request.travellers(), request.flexibilityDays(), request.maxBudget(), request.tripType(),
            request.preferredHotelRating());
        return watch;
    }

    @Transactional
    public TravelWatch pause(String email, UUID id) {
        TravelWatch watch = get(email, id);
        watch.setStatus(TravelWatchStatus.PAUSED);
        return watch;
    }

    @Transactional
    public TravelWatch resume(String email, UUID id) {
        TravelWatch watch = get(email, id);
        watch.setStatus(TravelWatchStatus.ACTIVE);
        return watch;
    }

    @Transactional
    public void delete(String email, UUID id) {
        watches.delete(get(email, id));
    }

    private void validateDates(TravelWatchRequest request) {
        if (!request.endDate().isAfter(request.startDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }
    }
}
