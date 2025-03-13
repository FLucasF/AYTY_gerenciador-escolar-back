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

    /**
     * Gerar um token JWT para um usuário autenticado.
     *
     * Este método gera um token JWT contendo o nome de usuário e sua role (permissão),
     * com um tempo de expiração definido.
     *
     * @param userDetails - Detalhes do usuário autenticado.
     * @return String - Token JWT assinado e pronto para uso.
     */
    public String generateAccessToken(UserDetails userDetails) {
        logger.info("Gerando token JWT para o usuário: {}", userDetails.getUsername());

        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withClaim("role", userDetails.getAuthorities().iterator().next().getAuthority())
                .withExpiresAt(Instant.now().plus(20, ChronoUnit.MINUTES))
                .sign(Algorithm.HMAC256(accessSecret));
    }

    /**
     * Validar um token JWT.
     *
     * Este método verifica se um token JWT é válido e assinado corretamente, garantindo
     * que não tenha sido alterado ou expirado.
     *
     * @param token - Token JWT a ser validado.
     * @return DecodedJWT - Retorna o objeto decodificado do JWT se for válido.
     * @throws RuntimeException - Se o token for inválido ou expirado.
     */
    public DecodedJWT validateToken(String token) {
        try {
            logger.info("Validando o token JWT.");
            return JWT.require(Algorithm.HMAC256(accessSecret)).build().verify(token);
        } catch (JWTVerificationException e) {
            logger.error("Erro ao validar o token JWT: {}", e.getMessage());
            throw new RuntimeException("Token inválido ou expirado!");
        }
    }

    /**
     * Extrair o nome de usuário de um token JWT.
     *
     * @param token - Token JWT do qual o nome de usuário será extraído.
     * @return String - Retorna o nome de usuário presente no token.
     */
    public String extractUsername(String token) {
        String username = validateToken(token).getSubject();
        logger.info("Usuário extraído do token JWT: {}", username);
        return username;
    }
}
