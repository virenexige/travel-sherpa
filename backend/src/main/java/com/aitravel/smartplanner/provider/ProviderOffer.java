package com.aitravel.smartplanner.provider;

import java.math.BigDecimal;

public record ProviderOffer(
    String providerName,
    String destination,
    String departureAirport,
    String arrivalAirport,
    BigDecimal flightPrice,
    BigDecimal hotelPrice,
    BigDecimal packagePrice,
    String currency,
    String resultUrl
) {
}
