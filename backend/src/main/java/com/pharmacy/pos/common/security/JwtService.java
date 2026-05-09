package com.pharmacy.pos.common.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * JwtService
 *
 * Responsible for:
 *   1. Generating access tokens (short-lived, 15 min)
 *   2. Generating refresh tokens (long-lived, 7 days)
 *   3. Validating and parsing tokens
 *
 * Token claims include:
 *   - sub  : user email
 *   - uid  : user ID
 *   - role : user role name
 *   - bid  : branch ID (null for OWNER/ADMIN)
 *   - type : "access" | "refresh"
 */
@Slf4j
@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.access-token-expiry-ms}")
    private long accessTokenExpiryMs;

    @Value("${app.jwt.refresh-token-expiry-ms}")
    private long refreshTokenExpiryMs;

    // ── Token Generation ─────────────────────────────────────────────

    public String generateAccessToken(UserPrincipal principal) {
        return buildToken(principal, accessTokenExpiryMs, "access");
    }

    public String generateRefreshToken(UserPrincipal principal) {
        return buildToken(principal, refreshTokenExpiryMs, "refresh");
    }

    private String buildToken(UserPrincipal principal, long expiryMs, String type) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(principal.getEmail())
                .claims(Map.of(
                    "uid",  principal.getId(),
                    "role", principal.getRole().name(),
                    "bid",  principal.getBranchId() != null ? principal.getBranchId() : "",
                    "type", type
                ))
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiryMs))
                .signWith(getSigningKey())
                .compact();
    }

    // ── Token Validation ─────────────────────────────────────────────

    public boolean isTokenValid(String token, UserPrincipal principal) {
        try {
            String email = extractEmail(token);
            return email.equals(principal.getEmail()) && !isTokenExpired(token);
        } catch (JwtException e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean isAccessToken(String token) {
        return "access".equals(extractClaim(token, c -> c.get("type", String.class)));
    }

    public boolean isRefreshToken(String token) {
        return "refresh".equals(extractClaim(token, c -> c.get("type", String.class)));
    }

    // ── Claim Extraction ─────────────────────────────────────────────

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Long extractUserId(String token) {
        return extractClaim(token, c -> c.get("uid", Long.class));
    }

    public String extractRole(String token) {
        return extractClaim(token, c -> c.get("role", String.class));
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsResolver.apply(claims);
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
