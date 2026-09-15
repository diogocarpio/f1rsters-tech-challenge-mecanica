package com.f1rsters.tech_challenge_mecanica.security;

import com.f1rsters.tech_challenge_mecanica.domain.StatusCliente;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
public class JwtService {

    private static final Set<StatusCliente> CLIENT_AUTH_STATUSES = EnumSet.of(
            StatusCliente.ATIVO,
            StatusCliente.APROVADO,
            StatusCliente.VERIFICADO
    );

    private final SecretKey key;
    private final long accessTokenMinutes;
    private final String issuer;
    private final String clientIssuer;
    private final String clientAudience;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.access-token-minutes}") long accessTokenMinutes,
            @Value("${security.jwt.issuer}") String issuer,
            @Value("${security.jwt.client-issuer}") String clientIssuer,
            @Value("${security.jwt.client-audience}") String clientAudience
    ) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessTokenMinutes = accessTokenMinutes;
        this.issuer = issuer;
        this.clientIssuer = clientIssuer;
        this.clientAudience = clientAudience;
    }

    public String generateToken(UserDetails userDetails) {
        Instant now = Instant.now();
        Instant expiration = now.plus(accessTokenMinutes, ChronoUnit.MINUTES);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .claim("roles", roles)
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isClientToken(String token) {
        Claims claims = extractAllClaims(token);
        String cpf = claims.get("cpf", String.class);
        if (cpf == null && !clientIssuer.equals(claims.getIssuer())) {
            return false;
        }
        if (!clientIssuer.equals(claims.getIssuer())) {
            throw new JwtException("Issuer do token de cliente invalido");
        }

        String status = claims.get("status", String.class);
        if (cpf == null || !cpf.equals(claims.getSubject())
                || claims.getAudience() == null || !claims.getAudience().contains(clientAudience)
                || status == null) {
            throw new JwtException("Token de cliente possui claims invalidas");
        }

        try {
            if (!CLIENT_AUTH_STATUSES.contains(StatusCliente.valueOf(status))) {
                throw new JwtException("Status do cliente nao permite autenticacao");
            }
        } catch (IllegalArgumentException ex) {
            throw new JwtException("Status do cliente invalido", ex);
        }

        return true;
    }

    public long getExpirationInSeconds() {
        return accessTokenMinutes * 60;
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
