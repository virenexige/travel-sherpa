package com.aitravel.smartplanner.pricing;

import com.aitravel.smartplanner.tripwatch.TravelWatch;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "price_history")
public class PriceHistory {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_watch_id")
    private TravelWatch travelWatch;
    private String providerName;
    private BigDecimal packagePrice;
    private BigDecimal flightPrice;
    private BigDecimal hotelPrice;
    private String currency;
    private Instant searchedAt;

    protected PriceHistory() {
    }

    public PriceHistory(UUID id, TravelWatch travelWatch, String providerName, BigDecimal packagePrice,
                        BigDecimal flightPrice, BigDecimal hotelPrice, String currency, Instant searchedAt) {
        this.id = id;
        this.travelWatch = travelWatch;
        this.providerName = providerName;
        this.packagePrice = packagePrice;
        this.flightPrice = flightPrice;
        this.hotelPrice = hotelPrice;
        this.currency = currency;
        this.searchedAt = searchedAt;
    }

    public UUID getId() { return id; }
    public String getProviderName() { return providerName; }
    public BigDecimal getPackagePrice() { return packagePrice; }
    public BigDecimal getFlightPrice() { return flightPrice; }
    public BigDecimal getHotelPrice() { return hotelPrice; }
    public String getCurrency() { return currency; }
    public Instant getSearchedAt() { return searchedAt; }
}
