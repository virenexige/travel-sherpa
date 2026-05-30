package com.aitravel.smartplanner.provider;

public record ProviderSignal(
    String providerName,
    String status,
    String currency,
    String message
) {
}
