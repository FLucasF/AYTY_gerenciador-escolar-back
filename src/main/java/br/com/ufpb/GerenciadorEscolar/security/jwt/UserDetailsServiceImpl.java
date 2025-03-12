package br.com.ufpb.GerenciadorEscolar.security.jwt;

import br.com.ufpb.GerenciadorEscolar.model.entity.UserLogin;
import br.com.ufpb.GerenciadorEscolar.repository.UserLoginRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final UserLoginRepository userLoginRepository;

    public UserDetailsServiceImpl(UserLoginRepository userLoginRepository) {
        this.userLoginRepository = userLoginRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("Buscando usuário pelo email: {}", email);

        UserLogin userLogin = userLoginRepository.findByEmailAndAtivoTrue(email)
                .orElseThrow(() -> {
                    logger.error("Usuário não encontrado ou inativo: {}", email);
                    return new UsernameNotFoundException("Usuário não encontrado ou inativo: " + email);
                });

        logger.info("Usuário encontrado: {}", email);
        return new org.springframework.security.core.userdetails.User(
                userLogin.getEmail(),
                userLogin.getSenha(),
                List.of(new SimpleGrantedAuthority(userLogin.getUsuario().getRole()))
        );
    }
}
