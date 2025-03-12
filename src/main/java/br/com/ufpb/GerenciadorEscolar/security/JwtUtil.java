package br.com.ufpb.GerenciadorEscolar.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class JwtUtil {

    @Value("${jwt.access.secret}")
    private String accessSecret;

    public String generateAccessToken(UserDetails userDetails) {
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withClaim("role", userDetails.getAuthorities().iterator().next().getAuthority())
                .withExpiresAt(Instant.now().plus(10, ChronoUnit.MINUTES))
                .sign(Algorithm.HMAC256(accessSecret));
    }

    public DecodedJWT validateToken(String token) {
        try {
            return JWT.require(Algorithm.HMAC256(accessSecret)).build().verify(token);
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Token inválido ou expirado!");
        }
    }

    public String extractUsername(String token) {
        return validateToken(token).getSubject();
    }
}
