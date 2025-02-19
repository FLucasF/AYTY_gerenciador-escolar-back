package br.com.ufpb.GerenciadorEscolar.security;

import br.com.ufpb.GerenciadorEscolar.model.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Service
public class TokenProvider {

    @Value("${app.secret}")
    private String secret;

    private Algorithm algorithm;

    @PostConstruct
    public void setUp() {
        // Cria a chave de assinatura usando a chave secreta
        algorithm = Algorithm.HMAC256(secret.getBytes());
    }

    public String generateToken(Usuario usuario) {
        try {
            return JWT.create()
                    .withSubject(usuario.getEmail())
                    .withClaim("role", usuario.getClass().getSimpleName())
                    .withIssuedAt(Date.from(Instant.now()))
                    .withExpiresAt(Date.from(LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.of("-03:00"))))
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw new RuntimeException("Erro na criação do token: " + e.getMessage());
        }
    }

    public String getSubjectByToken(String token) {
        try {
            return JWT.require(algorithm)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Token inválido: " + e.getMessage());
        }
    }
}
