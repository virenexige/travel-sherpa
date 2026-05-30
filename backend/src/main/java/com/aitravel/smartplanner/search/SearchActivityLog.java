package com.aitravel.smartplanner.search;

import com.aitravel.smartplanner.tripwatch.TravelWatch;
import jakarta.persistence.Column;
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
@Table(name = "search_activity_logs")
public class SearchActivityLog {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_watch_id")
    private TravelWatch travelWatch;
    private String providerName;
    private String searchType;
    private String departureLocation;
    private String destination;
    private String departureAirport;
    private String arrivalAirport;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private int offersReturned;
    private BigDecimal cheapestPackagePrice;
    private String currency;
    @Column(columnDefinition = "text")
    private String message;
    private Instant searchedAt;

    protected SearchActivityLog() {
    }

    public SearchActivityLog(UUID id, TravelWatch travelWatch, String providerName, String searchType,
                             String departureLocation, String destination, String departureAirport,
                             String arrivalAirport, LocalDate startDate, LocalDate endDate, String status,
                             int offersReturned, BigDecimal cheapestPackagePrice, String currency,
                             String message, Instant searchedAt) {
        this.id = id;
        this.travelWatch = travelWatch;
        this.providerName = providerName;
        this.searchType = searchType;
        this.departureLocation = departureLocation;
        this.destination = destination;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.offersReturned = offersReturned;
        this.cheapestPackagePrice = cheapestPackagePrice;
        this.currency = currency;
        this.message = message;
        this.searchedAt = searchedAt;
    }

    public UUID getId() { return id; }
    public String getProviderName() { return providerName; }
    public String getSearchType() { return searchType; }
    public String getDepartureLocation() { return departureLocation; }
    public String getDestination() { return destination; }
    public String getDepartureAirport() { return departureAirport; }
    public String getArrivalAirport() { return arrivalAirport; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getStatus() { return status; }
    public int getOffersReturned() { return offersReturned; }
    public BigDecimal getCheapestPackagePrice() { return cheapestPackagePrice; }
    public String getCurrency() { return currency; }
    public String getMessage() { return message; }
    public Instant getSearchedAt() { return searchedAt; }
}
