package com.aitravel.smartplanner.tripwatch;

import com.aitravel.smartplanner.user.AppUser;
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
    private int travellers;
    private int flexibilityDays;
    private BigDecimal maxBudget;
    private String tripType;
    private Integer preferredHotelRating;
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
        this.maxBudget = maxBudget;
        this.tripType = tripType;
        this.preferredHotelRating = preferredHotelRating;
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
    public int getTravellers() { return travellers; }
    public int getFlexibilityDays() { return flexibilityDays; }
    public BigDecimal getMaxBudget() { return maxBudget; }
    public String getTripType() { return tripType; }
    public Integer getPreferredHotelRating() { return preferredHotelRating; }
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

    public void setStatus(TravelWatchStatus status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }
}
