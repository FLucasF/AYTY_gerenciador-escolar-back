package br.com.ufpb.GerenciadorEscolar.security.jwt;

import br.com.ufpb.GerenciadorEscolar.model.dto.userLogin.AuthenticationRequest;
import br.com.ufpb.GerenciadorEscolar.model.dto.userLogin.AuthenticationResponse;
import br.com.ufpb.GerenciadorEscolar.model.entity.UserLogin;
import br.com.ufpb.GerenciadorEscolar.repository.UserLoginRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthenticationService {

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

    /**
     * Realizar login de um usuário e gerar um token JWT.
     *
     * Este método autentica um usuário verificando suas credenciais e, se forem válidas,
     * gera um token de acesso JWT para permitir a autenticação nas requisições subsequentes.
     *
     * @param authRequest - Objeto contendo as credenciais de login (e-mail e senha).
     * @return AuthenticationResponse - Retorna um token JWT válido e os detalhes do usuário autenticado.
     * @throws BadCredentialsException - Se as credenciais fornecidas forem inválidas.
     */
    public AuthenticationResponse login(AuthenticationRequest authRequest) {
        log.info("Tentativa de login para o email: {}", authRequest.email());

        UserLogin userLogin = userLoginRepository.findByEmailAndAtivoTrue(authRequest.email())
                .orElseThrow(() -> {
                    log.error("Credenciais inválidas para o email: {}", authRequest.email());
                    return new BadCredentialsException("Credenciais inválidas!");
                });

        if (!passwordEncoder.matches(authRequest.senha(), userLogin.getSenha())) {
            log.error("Senha incorreta para o email: {}", authRequest.email());
            throw new BadCredentialsException("Senha incorreta!");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.email());
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        log.info("Token JWT gerado para o email: {}", authRequest.email());

        return new AuthenticationResponse(accessToken, userLogin.getUsuario().getId(), userLogin.getUsuario().getRole());
    }

    public void logout() {
        log.info("Usuário desconectado com sucesso.");
    }

}
