package com.tetris.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    // TODO: Replace deprecated SignatureAlgorithm.HS512 with modern algorithm (HS256 or EdDSA)
    // TODO: Implement proper key management (KeyStore, environment variables, or HSM)
    // TODO: Add key rotation mechanism for enhanced security
    // TODO: Add token refresh functionality with separate refresh tokens
    // TODO: Implement token blacklisting/revocation mechanism (Redis/database)
    // TODO: Add additional claims (roles, permissions, user ID, session ID)
    // TODO: Add configurable token expiration per user type/role
    // TODO: Add JWT ID (jti) claim for unique token identification
    // TODO: Add issuer and audience validation
    // TODO: Add proper secret key validation (length, complexity requirements)

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateTokenFromUsername(String username) {
        // TODO: Add user role/permission claims to token
        // TODO: Add session ID claim for tracking
        // TODO: Add configurable expiration based on user type
        // TODO: Add JWT ID (jti) for unique identification
        // TODO: Add issuer and audience claims
        // TODO: Add custom claims for game-specific data
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        // TODO: Add token blacklisting check before parsing
        // TODO: Add issuer and audience validation
        // TODO: Add expiration time validation with clock skew tolerance
        // TODO: Add JWT ID validation for uniqueness
        // TODO: Add proper exception handling for different JWT errors
        // TODO: Add logging for token validation attempts
        // TODO: Add caching for frequently accessed tokens
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        // TODO: Replace generic exception handling with specific JWT exception types
        // TODO: Add token blacklisting check
        // TODO: Add issuer and audience validation
        // TODO: Add clock skew tolerance for expiration validation
        // TODO: Add JWT ID uniqueness validation
        // TODO: Add proper logging with security event levels
        // TODO: Add metrics/monitoring for token validation failures
        // TODO: Add rate limiting for repeated validation failures
        // TODO: Add configurable validation strictness levels
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (Exception ex) {
            System.out.println("Invalid JWT token: " + ex.getMessage());
        }
        return false;
    }
}
