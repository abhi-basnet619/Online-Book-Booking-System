package com.bookstore.security;

import com.bookstore.domain.enums.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private final SecretKey secretKey;

    public JwtUtil(@Value("${app.security.jwt.secret}") String secret) {
        // Accept either Base64 or raw long secret; if not Base64 decodable, use bytes directly
        SecretKey key;
        try {
            byte[] decoded = Decoders.BASE64.decode(secret);
            key = Keys.hmacShaKeyFor(decoded);
        } catch (Exception ignored) {
            key = Keys.hmacShaKeyFor(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }
        this.secretKey = key;
    }

    public String generateToken(String username, Role role, long expirationMinutes) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expirationMinutes * 60);

        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claims(Map.of("role", role.name()))
                .signWith(secretKey)
                .compact();
    }

    public Jws<Claims> parseAndValidate(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
        } catch (ExpiredJwtException eje) {
            throw new JwtExpiredException("JWT token is expired");
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtInvalidException("JWT token is invalid");
        }
    }

    public String extractUsername(String token) {
        return parseAndValidate(token).getPayload().getSubject();
    }

    public String extractRole(String token) {
        Object role = parseAndValidate(token).getPayload().get("role");
        return role != null ? role.toString() : null;
    }

    public long extractExpiresAtEpochMs(String token) {
        Date exp = parseAndValidate(token).getPayload().getExpiration();
        return exp.getTime();
    }
}
