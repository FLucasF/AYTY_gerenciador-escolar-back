package br.com.ufpb.GerenciadorEscolar.security;

import br.com.ufpb.GerenciadorEscolar.dto.userLogin.AuthenticationRequest;
import br.com.ufpb.GerenciadorEscolar.dto.userLogin.AuthenticationResponse;
import br.com.ufpb.GerenciadorEscolar.model.RefreshToken;
import br.com.ufpb.GerenciadorEscolar.model.UserLogin;
import br.com.ufpb.GerenciadorEscolar.repository.UserLoginRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final PasswordEncoder passwordEncoder;
    private final UserLoginRepository userLoginRepository;
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final RefreshTokenService refreshTokenService;

    // Tempo de validade do refresh token em milissegundos (valor definido na propriedade jwt.refresh.expiration)
    private final Long refreshTokenExpiration;

    public AuthenticationService(PasswordEncoder passwordEncoder,
                                 UserLoginRepository userLoginRepository,
                                 JwtUtil jwtUtil,
                                 UserDetailsServiceImpl userDetailsService,
                                 RefreshTokenService refreshTokenService,
                                 @org.springframework.beans.factory.annotation.Value("${jwt.refresh.expiration}") Long refreshTokenExpiration) {
        this.userLoginRepository = userLoginRepository;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.refreshTokenService = refreshTokenService;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    /**
     * Realiza o login, gera o access token e o refresh token e define o cookie para o refresh token.
     */
    public AuthenticationResponse login(AuthenticationRequest authRequest, HttpServletResponse response) {
        logger.info("Tentativa de login para o email: {}", authRequest.email());

        UserLogin userLogin = userLoginRepository.findByEmailAndAtivoTrue(authRequest.email())
                .orElseThrow(() -> {
                    logger.warn("Falha no login: usuário não encontrado para o email {}", authRequest.email());
                    return new BadCredentialsException("Credenciais inválidas!");
                });

        if (!passwordEncoder.matches(authRequest.senha(), userLogin.getSenha())) {
            logger.warn("Falha no login: senha incorreta para o email {}", authRequest.email());
            throw new BadCredentialsException("Senha incorreta!");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.email());
        String accessToken = jwtUtil.generateAccessToken(userDetails);

        // Gera um novo refresh token para o usuário (usando o ID do UserLogin)
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userLogin.getId());

        // Define o cookie para o refresh token (caso opte por usar cookies httpOnly)
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken.getId().toString());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true); // Se estiver em HTTPS
        refreshCookie.setPath("/auth/refresh");
        refreshCookie.setMaxAge((int) (refreshTokenExpiration / 1000));
        response.addCookie(refreshCookie);

        logger.info("Login bem-sucedido para o email: {}", authRequest.email());
        return new AuthenticationResponse(
                accessToken,
                refreshToken.getId().toString(),  // Valor do refresh token (UUID)
                userLogin.getUsuario().getId(),     // ID da entidade Usuario associada
                userLogin.getUsuario().getRole()   // Role da entidade Usuario
        );
    }

    /**
     * Realiza a renovação dos tokens: valida o refresh token, revoga-o, gera um novo access token e um novo refresh token.
     */
    public AuthenticationResponse refreshToken(String refreshTokenValue, HttpServletResponse response) {
        logger.info("Requisição para refresh token: {}", refreshTokenValue);

        Optional<RefreshToken> validToken = refreshTokenService.validateRefreshToken(refreshTokenValue);

        if (validToken.isEmpty()) {
            logger.warn("Refresh Token inválido ou expirado: {}", refreshTokenValue);
            throw new BadCredentialsException("Refresh Token inválido ou expirado.");
        }

        RefreshToken oldToken = validToken.get();
        logger.info("Refresh Token válido encontrado para o usuário: {}", oldToken.getUser().getUsuario().getEmail());

        // Revoga o refresh token usado
        refreshTokenService.revokeRefreshToken(oldToken.getId().toString());

        // Gera um novo access token e um novo refresh token
        UserDetails userDetails = userDetailsService.loadUserByUsername(oldToken.getUser().getUsuario().getEmail());
        String newAccessToken = jwtUtil.generateAccessToken(userDetails);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(oldToken.getUser().getUsuario().getId());

        // Atualiza o cookie com o novo refresh token
        Cookie refreshCookie = new Cookie("refreshToken", newRefreshToken.getId().toString());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/auth/refresh");
        refreshCookie.setMaxAge((int) (refreshTokenExpiration / 1000));
        response.addCookie(refreshCookie);

        logger.info("Novo refresh token gerado para o usuário: {}", oldToken.getUser().getUsuario().getEmail());
        return new AuthenticationResponse(
                newAccessToken,
                newRefreshToken.getId().toString(),
                oldToken.getUser().getUsuario().getId(),
                oldToken.getUser().getUsuario().getRole()
        );
    }

    /**
     * Revoga todos os refresh tokens ativos do usuário (logout).
     */
    // Método de logout atualizado:
    public void logout(String refreshTokenValue) {
        try {
            UUID tokenId = UUID.fromString(refreshTokenValue);
            Optional<RefreshToken> tokenOpt = refreshTokenService.findById(tokenId);
            if (tokenOpt.isPresent()){
                Long userId = tokenOpt.get().getUser().getId();
                refreshTokenService.revokeAllTokensByUser(userId);
            }
        } catch (IllegalArgumentException e) {
            // refreshToken inválido; podemos ignorar ou logar o ocorrido.
            logger.warn("Refresh token inválido no logout: {}", refreshTokenValue);
        }
    }
}
