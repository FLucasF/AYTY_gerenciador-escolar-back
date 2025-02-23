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
import java.util.Base64;

@Service
public class TokenProvider {

    private final String secret;
    private Algorithm algorithm;

    public TokenProvider(@Value("minhaChaveSecreta123456") String secret) {
        this.secret = secret;
    }

    @PostConstruct
    public void setUp() {
        Base64.getEncoder().encode(secret.getBytes());
        this.algorithm = Algorithm.HMAC256(secret.getBytes());
    }

    public String generateAccessToken(Usuario usuario) {
        try {
            System.out.println("Gerando token para o usuário: " + usuario.getEmail() +
                    ", classe: " + usuario.getClass().getSimpleName());
            String role = "ROLE_" + usuario.getClass().getSimpleName().toUpperCase();
            Instant expiration = LocalDateTime.now().plusMinutes(15).toInstant(ZoneOffset.of("-03:00"));

            String token = JWT.create()
                    .withSubject(usuario.getEmail())
                    .withClaim("role", role)
                    .withIssuedAt(Instant.now())
                    .withExpiresAt(expiration)
                    .sign(algorithm);
            System.out.println("Token gerado: " + token);
            return token;
        } catch (JWTCreationException e) {
            throw new RuntimeException("Erro na criação do access token: " + e.getMessage());
        }
    }


    public String generateRefreshToken(Usuario usuario) {
        try {
            String role = "ROLE_" + usuario.getClass().getSimpleName().toUpperCase();
            Instant expiration = LocalDateTime.now().plusDays(7).toInstant(ZoneOffset.of("-03:00"));

            return JWT.create()
                    .withSubject(usuario.getEmail())
                    .withClaim("role", role)
                    .withIssuedAt(Instant.now())
                    .withExpiresAt(expiration)
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw new RuntimeException("Erro na criação do refresh token: " + e.getMessage());
        }
    }

    public String getSubjectByToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new RuntimeException("Token não fornecido.");
        }
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
