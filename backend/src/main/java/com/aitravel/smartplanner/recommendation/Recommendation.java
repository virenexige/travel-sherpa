package com.aitravel.smartplanner.recommendation;

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
import java.util.UUID;

@Entity
@Table(name = "recommendations")
public class Recommendation {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_watch_id")
    private TravelWatch travelWatch;
    private String title;
    @Column(columnDefinition = "text")
    private String explanation;
    private String recommendationType;
    private BigDecimal confidenceScore;
    private BigDecimal estimatedSaving;
    private Instant createdAt;

    protected Recommendation() {
    }

    public Recommendation(UUID id, TravelWatch travelWatch, String title, String explanation, String recommendationType,
                          BigDecimal confidenceScore, BigDecimal estimatedSaving, Instant createdAt) {
        this.id = id;
        this.travelWatch = travelWatch;
        this.title = title;
        this.explanation = explanation;
        this.recommendationType = recommendationType;
        this.confidenceScore = confidenceScore;
        this.estimatedSaving = estimatedSaving;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getExplanation() { return explanation; }
    public String getRecommendationType() { return recommendationType; }
    public BigDecimal getConfidenceScore() { return confidenceScore; }
    public BigDecimal getEstimatedSaving() { return estimatedSaving; }
    public Instant getCreatedAt() { return createdAt; }
}
