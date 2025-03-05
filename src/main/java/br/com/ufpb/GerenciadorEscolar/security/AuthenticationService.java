package br.com.ufpb.GerenciadorEscolar.security;

import br.com.ufpb.GerenciadorEscolar.model.UserLogin;
import br.com.ufpb.GerenciadorEscolar.model.Usuario;
import br.com.ufpb.GerenciadorEscolar.repository.UserLoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class AuthenticationService {

    private final UserLoginRepository userLoginRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    @Autowired
    public AuthenticationService(UserLoginRepository userLoginRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userLoginRepository = userLoginRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String loginUsuario(String email, String senha) {
        logger.debug("Tentando autenticar o usuário com email: {}", email);

        UserLogin userLogin = userLoginRepository.findByEmailAndAtivoTrue(email)
                .orElseThrow(() -> {
                    logger.warn("Usuário não encontrado ou inativo para o email: {}", email);
                    return new UsernameNotFoundException("Credenciais inválidas!");
                });

        logger.debug("Usuário encontrado: {}", userLogin.getEmail());

        if (!passwordEncoder.matches(senha, userLogin.getSenha())) {
            logger.warn("Senha inválida para o usuário: {}", email);
            throw new UsernameNotFoundException("Credenciais inválidas!");
        }

        String token = jwtUtil.generateToken(loadUserByUsername(email), userLogin.getUsuario().getRole());
        logger.debug("Token JWT gerado com sucesso para o usuário: {}", email);

        return token;
    }

    // Método para carregar o UserDetails (detalhes do usuário) a partir do email
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Carregando detalhes do usuário para o email: {}", email);

        UserLogin userLogin = userLoginRepository.findByEmailAndAtivoTrue(email)
                .orElseThrow(() -> {
                    logger.warn("Usuário não encontrado ou inativo para o email: {}", email);
                    return new UsernameNotFoundException("Usuário não encontrado ou inativo: " + email);
                });

        Usuario usuario = userLogin.getUsuario();
        logger.debug("Usuário carregado: {} com role: {}", usuario.getEmail(), usuario.getRole());

        return new User(
                userLogin.getEmail(),
                userLogin.getSenha(),
                List.of(usuario::getRole)
        );
    }

    // Método para buscar o Usuario diretamente
    public Usuario buscarUsuarioPorEmail(String email) {
        logger.debug("Buscando usuário diretamente pelo email: {}", email);

        UserLogin userLogin = userLoginRepository.findByEmailAndAtivoTrue(email)
                .orElseThrow(() -> {
                    logger.warn("Usuário não encontrado ou inativo para o email: {}", email);
                    return new UsernameNotFoundException("Usuário não encontrado ou inativo: " + email);
                });

        return userLogin.getUsuario();
    }
}
