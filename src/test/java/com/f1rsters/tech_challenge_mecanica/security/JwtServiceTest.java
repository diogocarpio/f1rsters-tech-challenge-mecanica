package com.f1rsters.tech_challenge_mecanica.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        String secret = Base64.getEncoder().encodeToString("mySecretKeyForTestingPurposesThatIsLongEnough1234567890".getBytes());
        jwtService = new JwtService(secret, 60, "test-issuer");
        
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
    void shouldReturnExpirationInSeconds() {
        long expiration = jwtService.getExpirationInSeconds();
        
        assertEquals(3600, expiration);
    }
}
