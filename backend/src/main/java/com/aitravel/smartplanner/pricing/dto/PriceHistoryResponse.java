package com.aitravel.smartplanner.pricing.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PriceHistoryResponse(
    UUID id,
    String providerName,
    BigDecimal packagePrice,
    BigDecimal flightPrice,
    BigDecimal hotelPrice,
    String currency,
    Instant searchedAt
) {
}
