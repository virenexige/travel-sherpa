package com.aitravel.smartplanner.tripwatch;

import com.aitravel.smartplanner.user.AppUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "travel_watches")
public class TravelWatch {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user;
    private String departureLocation;
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    @Column(name = "range2_start_date")
    private LocalDate range2StartDate;
    @Column(name = "range2_end_date")
    private LocalDate range2EndDate;
    @Column(name = "range3_start_date")
    private LocalDate range3StartDate;
    @Column(name = "range3_end_date")
    private LocalDate range3EndDate;
    private int travellers;
    private int flexibilityDays;
    @Column(name = "start_days_early")
    private int startDaysEarly;
    @Column(name = "start_days_late")
    private int startDaysLate;
    @Column(name = "finish_days_early")
    private int finishDaysEarly;
    @Column(name = "finish_days_late")
    private int finishDaysLate;
    @Column(name = "duration_increase_days")
    private int durationIncreaseDays;
    private BigDecimal maxBudget;
    private String tripType;
    private Integer preferredHotelRating;
    private String travelProductType;
    private String cabinClass;
    private boolean bucketList;
    private String bucketListName;
    private LocalDate earliestStartDate;
    private LocalDate latestEndDate;
    @Column(columnDefinition = "text")
    private String notes;
    @Enumerated(EnumType.STRING)
    private TravelWatchStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    protected TravelWatch() {
    }

    public TravelWatch(UUID id, AppUser user, String departureLocation, String destination, LocalDate startDate,
                       LocalDate endDate, int travellers, int flexibilityDays, BigDecimal maxBudget,
                       String tripType, Integer preferredHotelRating, TravelWatchStatus status,
                       Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.user = user;
        this.departureLocation = departureLocation;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.travellers = travellers;
        this.flexibilityDays = flexibilityDays;
        this.startDaysEarly = flexibilityDays;
        this.startDaysLate = flexibilityDays;
        this.finishDaysEarly = 0;
        this.finishDaysLate = 0;
        this.durationIncreaseDays = 0;
        this.maxBudget = maxBudget;
        this.tripType = tripType;
        this.preferredHotelRating = preferredHotelRating;
        this.travelProductType = "PACKAGE";
        this.cabinClass = "ALL";
        this.bucketList = false;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public AppUser getUser() { return user; }
    public String getDepartureLocation() { return departureLocation; }
    public String getDestination() { return destination; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public LocalDate getRange2StartDate() { return range2StartDate; }
    public LocalDate getRange2EndDate() { return range2EndDate; }
    public LocalDate getRange3StartDate() { return range3StartDate; }
    public LocalDate getRange3EndDate() { return range3EndDate; }
    public int getTravellers() { return travellers; }
    public int getFlexibilityDays() { return flexibilityDays; }
    public int getStartDaysEarly() { return startDaysEarly; }
    public int getStartDaysLate() { return startDaysLate; }
    public int getFinishDaysEarly() { return finishDaysEarly; }
    public int getFinishDaysLate() { return finishDaysLate; }
    public int getDurationIncreaseDays() { return durationIncreaseDays; }
    public BigDecimal getMaxBudget() { return maxBudget; }
    public String getTripType() { return tripType; }
    public Integer getPreferredHotelRating() { return preferredHotelRating; }
    public String getTravelProductType() { return travelProductType; }
    public String getCabinClass() { return cabinClass; }
    public boolean isBucketList() { return bucketList; }
    public String getBucketListName() { return bucketListName; }
    public LocalDate getEarliestStartDate() { return earliestStartDate; }
    public LocalDate getLatestEndDate() { return latestEndDate; }
    public String getNotes() { return notes; }
    public TravelWatchStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void update(String departureLocation, String destination, LocalDate startDate, LocalDate endDate,
                       int travellers, int flexibilityDays, BigDecimal maxBudget, String tripType,
                       Integer preferredHotelRating) {
        this.departureLocation = departureLocation;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.travellers = travellers;
        this.flexibilityDays = flexibilityDays;
        this.maxBudget = maxBudget;
        this.tripType = tripType;
        this.preferredHotelRating = preferredHotelRating;
        this.updatedAt = Instant.now();
    }

    public void updateDateOptions(LocalDate range2StartDate, LocalDate range2EndDate,
                                  LocalDate range3StartDate, LocalDate range3EndDate,
                                  int startDaysEarly, int startDaysLate,
                                  int finishDaysEarly, int finishDaysLate,
                                  int durationIncreaseDays) {
        this.range2StartDate = range2StartDate;
        this.range2EndDate = range2EndDate;
        this.range3StartDate = range3StartDate;
        this.range3EndDate = range3EndDate;
        this.startDaysEarly = startDaysEarly;
        this.startDaysLate = startDaysLate;
        this.finishDaysEarly = finishDaysEarly;
        this.finishDaysLate = finishDaysLate;
        this.durationIncreaseDays = durationIncreaseDays;
        this.flexibilityDays = Math.max(Math.max(startDaysEarly, startDaysLate),
            Math.max(Math.max(finishDaysEarly, finishDaysLate), durationIncreaseDays));
        this.updatedAt = Instant.now();
    }

    public void updateFlightPreferences(String travelProductType, String cabinClass) {
        this.travelProductType = normalize(travelProductType, "PACKAGE");
        this.cabinClass = normalize(cabinClass, "ALL");
        this.updatedAt = Instant.now();
    }

    public void updateBucketList(boolean bucketList, String bucketListName, LocalDate earliestStartDate,
                                 LocalDate latestEndDate, String notes) {
        this.bucketList = bucketList;
        this.bucketListName = bucketListName;
        this.earliestStartDate = earliestStartDate;
        this.latestEndDate = latestEndDate;
        this.notes = notes;
        this.updatedAt = Instant.now();
    }

    public void setStatus(TravelWatchStatus status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }

    private String normalize(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
