package com.aitravel.smartplanner.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final SecurityProperties properties;
    private final SecretKey key;

    public JwtService(SecurityProperties properties) {
        this.properties = properties;
        this.key = Keys.hmacShaKeyFor(properties.jwtSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(String email, String name) {
        Instant now = Instant.now();
        return Jwts.builder()
            .subject(email)
            .claim("name", name)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(properties.tokenTtlHours() * 3600)))
            .signWith(key)
            .compact();
    }

    public AuthenticatedUser parse(String token) {
        var claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        return new AuthenticatedUser(claims.getSubject(), claims.get("name", String.class));
    }
}
