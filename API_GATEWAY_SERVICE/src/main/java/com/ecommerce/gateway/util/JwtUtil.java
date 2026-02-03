package com.ecommerce.gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtUtil {

    // IMPORTANT: For HS384, the secret must be at least 48 characters long.
    // Replace the default value below with a secure environment variable in production.
    @Value("${jwt.secret.key:this-is-a-very-long-and-secure-secret-key-that-is-at-least-48-characters}")
    private String secret;

    private SecretKey key;

    @jakarta.annotation.PostConstruct
    public void init() {
        // Initializes the cryptographic key using the provided secret string.
        // Ensure this secret is identical to the one used in your User Service.
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Extracts and validates the claims from a given JWT token.
     * * @param token The JWT string provided by the client.
     * @return The claims contained within the token.
     * @throws io.jsonwebtoken.JwtException if the token is invalid or expired.
     */
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}