package com.jdpadillac.jtaskboard.auth.infrastructure.out.adapters;

import com.jdpadillac.jtaskboard.auth.domain.model.User;
import com.jdpadillac.jtaskboard.auth.domain.port.out.GenerateTokenPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import javax.crypto.SecretKey;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Issues and verifies stateless HS256 JWT session tokens.
 */
@Component
public class JwtTokenAdapter implements GenerateTokenPort {

    private final SecretKey signingKey;
    private final long expirationMinutes;

    public JwtTokenAdapter(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-minutes}") long expirationMinutes
    ) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = expirationMinutes;
    }

    @Override
    public String generate(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.id().toString())
                .claim("email", user.email())
                .claim("name", user.firstName() + " " + user.lastName())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expirationMinutes, ChronoUnit.MINUTES)))
                .signWith(signingKey)
                .compact();
    }

    /**
     * Verifies the token signature and expiration, returning the carried principal.
     * Returns an empty optional when the token is missing, malformed, expired or tampered with.
     */
    public Optional<JwtPrincipal> parse(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return Optional.of(new JwtPrincipal(
                    UUID.fromString(claims.getSubject()),
                    claims.get("email", String.class)
            ));
        } catch (JwtException | IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    public record JwtPrincipal(UUID userId, String email) {
    }
}
