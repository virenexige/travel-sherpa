package com.aitravel.smartplanner.scheduler;

import com.aitravel.smartplanner.search.TravelSearchService;
import com.aitravel.smartplanner.tripwatch.TravelWatchRepository;
import com.aitravel.smartplanner.tripwatch.TravelWatchStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TravelWatchScheduler {
    private static final Logger log = LoggerFactory.getLogger(TravelWatchScheduler.class);
    private final TravelWatchRepository watches;
    private final TravelSearchService searches;

    public TravelWatchScheduler(TravelWatchRepository watches, TravelSearchService searches) {
        this.watches = watches;
        this.searches = searches;
    }

    @Scheduled(cron = "${app.scheduler.travel-watch-refresh-cron}")
    public void refreshActiveWatches() {
        var active = watches.findByStatus(TravelWatchStatus.ACTIVE);
        log.info("Refreshing {} active travel watches", active.size());
        active.forEach(searches::runSearch);
    }

    @Scheduled(cron = "0 30 6 * * *")
    public void refreshBucketListWatchesDaily() {
        var activeBucketList = watches.findByStatusAndBucketList(TravelWatchStatus.ACTIVE, true);
        log.info("Refreshing {} active bucket-list travel watches", activeBucketList.size());
        activeBucketList.forEach(searches::runSearch);
    }
}
