package br.com.ufpb.GerenciadorEscolar.security;

import br.com.ufpb.GerenciadorEscolar.model.Usuario;
import br.com.ufpb.GerenciadorEscolar.repository.UserLoginRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Service
public class AuthenticationService implements UserDetailsService {

    private final UserLoginRepository userLoginRepository;

    public AuthenticationService(UserLoginRepository userLoginRepository) {
        this.userLoginRepository = userLoginRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = userLoginRepository.findByEmailAndAtivoTrue(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        return new org.springframework.security.core.userdetails.User(
                usuario.getEmail(),
                usuario.getSenha(),
                List.of(new SimpleGrantedAuthority(usuario.getRole()))
        );
    }
}
