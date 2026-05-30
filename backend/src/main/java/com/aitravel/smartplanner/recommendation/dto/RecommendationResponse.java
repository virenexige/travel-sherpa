package com.aitravel.smartplanner.recommendation.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record RecommendationResponse(
    UUID id,
    String title,
    String explanation,
    String recommendationType,
    BigDecimal confidenceScore,
    BigDecimal estimatedSaving,
    Instant createdAt
) {
}
