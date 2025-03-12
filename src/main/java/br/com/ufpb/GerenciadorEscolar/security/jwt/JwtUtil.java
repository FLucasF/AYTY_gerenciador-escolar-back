package br.com.ufpb.GerenciadorEscolar.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.access.secret}")
    private String accessSecret;

    public String generateAccessToken(UserDetails userDetails) {
        logger.info("Gerando token JWT para o usuário: {}", userDetails.getUsername());

        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withClaim("role", userDetails.getAuthorities().iterator().next().getAuthority())
                .withExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS))  // Define a validade do access token
                .sign(Algorithm.HMAC256(accessSecret));
    }

    public DecodedJWT validateToken(String token) {
        try {
            logger.info("Validando o token JWT.");
            return JWT.require(Algorithm.HMAC256(accessSecret)).build().verify(token);
        } catch (JWTVerificationException e) {
            logger.error("Erro ao validar o token JWT: {}", e.getMessage());
            throw new RuntimeException("Token inválido ou expirado!");
        }
    }

    public String extractUsername(String token) {
        String username = validateToken(token).getSubject();
        logger.info("Usuário extraído do token JWT: {}", username);
        return username;
    }
}
