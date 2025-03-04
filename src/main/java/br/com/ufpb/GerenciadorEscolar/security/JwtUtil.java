package br.com.ufpb.GerenciadorEscolar.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtUtil {

    @Value("${app.token.key}")
    private String TOKEN_KEY;

    public String generateToken(UserDetails userDetails, String role) {
        Algorithm algorithm = Algorithm.HMAC256(TOKEN_KEY.getBytes());
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withClaim("role", role) // Adiciona a role ao token
                .withExpiresAt(expirationToken())
                .sign(algorithm);
    }

    private Instant expirationToken() {
        return LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.of("-03:00"));
    }

    public Optional<String> extractUsername(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_KEY.getBytes());
            return Optional.ofNullable(JWT.require(algorithm).build().verify(token).getSubject());
        } catch (JWTVerificationException e) {
            return Optional.empty();
        }
    }

    public Optional<String> extractRole(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_KEY.getBytes());
            return Optional.ofNullable(JWT.require(algorithm).build().verify(token).getClaim("role").asString());
        } catch (JWTVerificationException e) {
            return Optional.empty();
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        return extractUsername(token)
                .map(username -> username.equals(userDetails.getUsername()))
                .orElse(false);
    }

}
