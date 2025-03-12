package br.com.ufpb.GerenciadorEscolar.security;

import br.com.ufpb.GerenciadorEscolar.model.RefreshToken;
import br.com.ufpb.GerenciadorEscolar.repository.RefreshTokenRepository;
import br.com.ufpb.GerenciadorEscolar.repository.UserLoginRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserLoginRepository userLoginRepository;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpiration;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserLoginRepository userLoginRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userLoginRepository = userLoginRepository;
    }

    /**
     * Cria um novo refresh token para o usuário e revoga tokens antigos.
     */
    public RefreshToken createRefreshToken(Long userId) {
        // Revoga todos os tokens ativos do usuário
        revokeAllTokensByUser(userId);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userLoginRepository.findById(userId).orElseThrow());
        refreshToken.setExpiresAt(Instant.now().plusMillis(refreshTokenExpiration));
        refreshToken.setRevoked(false);
        // O campo id (UUID) é gerado automaticamente e será usado como valor do token.
        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findById(UUID tokenId) {
        return refreshTokenRepository.findById(tokenId);
    }

    /**
     * Valida se o refresh token existe, não está revogado e ainda não expirou.
     */
    public Optional<RefreshToken> validateRefreshToken(String token) {
        try {
            UUID tokenId = UUID.fromString(token);
            return refreshTokenRepository.findByIdAndRevokedFalseAndExpiresAtAfter(tokenId, Instant.now());
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    /**
     * Revoga um refresh token (marca como revogado).
     */
    @Transactional
    public void revokeRefreshToken(String token) {
        try {
            UUID tokenId = UUID.fromString(token);
            refreshTokenRepository.findById(tokenId).ifPresent(rt -> {
                rt.setRevoked(true);
                refreshTokenRepository.save(rt);
            });
        } catch (IllegalArgumentException e) {
            // Token inválido: log ou tratamento adicional, se necessário.
        }
    }

    /**
     * Revoga (ou deleta) todos os tokens do usuário.
     */
    @Transactional
    public void revokeAllTokensByUser(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}
