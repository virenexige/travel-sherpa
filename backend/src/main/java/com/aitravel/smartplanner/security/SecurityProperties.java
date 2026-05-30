package com.aitravel.smartplanner.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(String jwtSecret, long tokenTtlHours) {
}
