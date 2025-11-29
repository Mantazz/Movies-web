package org.app.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    // Deprecated method so should be changed
    private final SecretKey SECRET_KEY = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256); // SECRET_KEY is used to validate JWT token


    // Generating JWT token for given username
    public String generateToken (String username) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .claims(claims)
                .subject(username) // Setting up the username as the subject of the token
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Setting token time for 10 hours
                .signWith(SECRET_KEY) // Signing the token with the secret key
                .compact();
    }

    // Extracts all claims from the token
    public Claims extractAllClaims (String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY) // Use secret key to verify claims
                .build()
                .parseSignedClaims(token) // Parse claims JWS (JSON web signature) from the token
                .getPayload(); // Retrieve claims
    }

    // Extracts username from the JWT token
    public String extractUsername (String token) {
        return extractAllClaims(token).getSubject();
    }

    private boolean isTokenExpired (String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public boolean validateToken (String token, String username) {
        final String extractedUsername = extractUsername(token);
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }
}
