package com.f1rsters.tech_challenge_mecanica.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private static final String CLIENT_ISSUER = "tech-challenge-auth-lambda";
    private static final String CLIENT_AUDIENCE = "tech-challenge-api";
    private static final String CPF = "52998224725";

    private JwtService jwtService;
    private UserDetails userDetails;
    private SecretKey key;

    @BeforeEach
    void setUp() {
        byte[] secretBytes = "mySecretKeyForTestingPurposesThatIsLongEnough1234567890".getBytes();
        String secret = Base64.getEncoder().encodeToString(secretBytes);
        key = Keys.hmacShaKeyFor(secretBytes);
        jwtService = new JwtService(secret, 60, "test-issuer", CLIENT_ISSUER, CLIENT_AUDIENCE);

        userDetails = User.withUsername("test@example.com")
                .password("password")
                .roles("ADMIN")
                .build();
    }

    @Test
    void shouldGenerateToken() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void shouldExtractUsername() {
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);

        assertEquals("test@example.com", username);
    }

    @Test
    void shouldValidateToken() {
        String token = jwtService.generateToken(userDetails);

        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void shouldIdentifyClientToken() {
        assertTrue(jwtService.isClientToken(generateClientToken(CLIENT_ISSUER, CLIENT_AUDIENCE, "ATIVO")));
    }

    @Test
    void shouldNotIdentifyInternalTokenAsClient() {
        assertFalse(jwtService.isClientToken(jwtService.generateToken(userDetails)));
    }

    @Test
    void shouldRejectClientTokenWithInvalidIssuer() {
        String token = generateClientToken("invalid-issuer", CLIENT_AUDIENCE, "ATIVO");

        assertThrows(JwtException.class, () -> jwtService.isClientToken(token));
    }

    @Test
    void shouldRejectClientTokenWithInvalidAudience() {
        String token = generateClientToken(CLIENT_ISSUER, "invalid-audience", "ATIVO");

        assertThrows(JwtException.class, () -> jwtService.isClientToken(token));
    }

    @Test
    void shouldRejectClientTokenWithBlockedStatus() {
        String token = generateClientToken(CLIENT_ISSUER, CLIENT_AUDIENCE, "BLOQUEADO");

        assertThrows(JwtException.class, () -> jwtService.isClientToken(token));
    }

    @Test
    void shouldReturnExpirationInSeconds() {
        long expiration = jwtService.getExpirationInSeconds();

        assertEquals(3600, expiration);
    }

    private String generateClientToken(String issuer, String audience, String status) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(CPF)
                .issuer(issuer)
                .audience().add(audience).and()
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(15, ChronoUnit.MINUTES)))
                .claim("cpf", CPF)
                .claim("status", status)
                .signWith(key)
                .compact();
    }
}
