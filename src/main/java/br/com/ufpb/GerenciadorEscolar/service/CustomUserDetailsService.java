package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.model.Administrador;
import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.model.Professor;
import br.com.ufpb.GerenciadorEscolar.model.Usuario;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AdministradorServiceInterface administradorService;
    private final ProfessorServiceInterface professorService;
    private final AlunoServiceInterface alunoService;

    public CustomUserDetailsService(AdministradorServiceInterface administradorService,
                                    ProfessorServiceInterface professorService,
                                    AlunoServiceInterface alunoService) {
        this.administradorService = administradorService;
        this.professorService = professorService;
        this.alunoService = alunoService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Usuario> usuarioOpt = administradorService.findByEmail(email)
                .map(Usuario.class::cast)
                .or(() -> professorService.findByEmail(email).map(Usuario.class::cast))
                .or(() -> alunoService.findByEmail(email).map(Usuario.class::cast));

        if (usuarioOpt.isEmpty()) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }
        Usuario usuario = usuarioOpt.get();
        // Retorna um UserDetails com a role adequada (você pode ajustar conforme seu design)
        return new org.springframework.security.core.userdetails.User(
                usuario.getEmail(),
                usuario.getSenha(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + definirRole(usuario)))
        );
    }

    private String definirRole(Usuario usuario) {
        if (usuario instanceof Administrador) {
            return "ADMINISTRADOR";
        } else if (usuario instanceof Professor) {
            return "PROFESSOR";
        } else if (usuario instanceof Aluno) {
            return "ALUNO";
        }
        return "USER";
    }
}
