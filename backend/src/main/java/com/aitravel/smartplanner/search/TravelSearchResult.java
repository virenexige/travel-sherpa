package com.aitravel.smartplanner.search;

import com.aitravel.smartplanner.tripwatch.TravelWatch;
import jakarta.persistence.Entity;
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
@Table(name = "travel_search_results")
public class TravelSearchResult {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_watch_id")
    private TravelWatch travelWatch;
    private String providerName;
    private String destination;
    private String departureAirport;
    private String arrivalAirport;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal flightPrice;
    private BigDecimal hotelPrice;
    private BigDecimal packagePrice;
    private String currency;
    private int dealScore;
    private String resultUrl;
    private Instant searchedAt;

    protected TravelSearchResult() {
    }

    public TravelSearchResult(UUID id, TravelWatch travelWatch, String providerName, String destination,
                              String departureAirport, String arrivalAirport, LocalDate startDate, LocalDate endDate,
                              BigDecimal flightPrice, BigDecimal hotelPrice, BigDecimal packagePrice,
                              String currency, int dealScore, String resultUrl, Instant searchedAt) {
        this.id = id;
        this.travelWatch = travelWatch;
        this.providerName = providerName;
        this.destination = destination;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.startDate = startDate;
        this.endDate = endDate;
        this.flightPrice = flightPrice;
        this.hotelPrice = hotelPrice;
        this.packagePrice = packagePrice;
        this.currency = currency;
        this.dealScore = dealScore;
        this.resultUrl = resultUrl;
        this.searchedAt = searchedAt;
    }

    public UUID getId() { return id; }
    public TravelWatch getTravelWatch() { return travelWatch; }
    public String getProviderName() { return providerName; }
    public String getDestination() { return destination; }
    public String getDepartureAirport() { return departureAirport; }
    public String getArrivalAirport() { return arrivalAirport; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public BigDecimal getFlightPrice() { return flightPrice; }
    public BigDecimal getHotelPrice() { return hotelPrice; }
    public BigDecimal getPackagePrice() { return packagePrice; }
    public String getCurrency() { return currency; }
    public int getDealScore() { return dealScore; }
    public String getResultUrl() { return resultUrl; }
    public Instant getSearchedAt() { return searchedAt; }
}
