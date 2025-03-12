package br.com.ufpb.GerenciadorEscolar.security.jwt;

import br.com.ufpb.GerenciadorEscolar.model.dto.userLogin.AuthenticationRequest;
import br.com.ufpb.GerenciadorEscolar.model.dto.userLogin.AuthenticationResponse;
import br.com.ufpb.GerenciadorEscolar.model.entity.UserLogin;
import br.com.ufpb.GerenciadorEscolar.repository.UserLoginRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final PasswordEncoder passwordEncoder;
    private final UserLoginRepository userLoginRepository;
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public AuthenticationService(PasswordEncoder passwordEncoder,
                                 UserLoginRepository userLoginRepository,
                                 JwtUtil jwtUtil,
                                 UserDetailsServiceImpl userDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.userLoginRepository = userLoginRepository;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    public AuthenticationResponse login(AuthenticationRequest authRequest) {
        logger.info("Tentativa de login para o email: {}", authRequest.email());

        UserLogin userLogin = userLoginRepository.findByEmailAndAtivoTrue(authRequest.email())
                .orElseThrow(() -> {
                    logger.error("Credenciais inválidas para o email: {}", authRequest.email());
                    return new BadCredentialsException("Credenciais inválidas!");
                });

        if (!passwordEncoder.matches(authRequest.senha(), userLogin.getSenha())) {
            logger.error("Senha incorreta para o email: {}", authRequest.email());
            throw new BadCredentialsException("Senha incorreta!");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.email());
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        logger.info("Token JWT gerado para o email: {}", authRequest.email());

        // Retorna o token JWT diretamente na resposta
        return new AuthenticationResponse(accessToken, userLogin.getUsuario().getId(), userLogin.getUsuario().getRole());
    }

    public void logout() {
        // No caso de logout, não há cookie a ser removido, mas podemos limpar qualquer estado do usuário, se necessário.
        logger.info("Usuário desconectado com sucesso.");
    }
}
