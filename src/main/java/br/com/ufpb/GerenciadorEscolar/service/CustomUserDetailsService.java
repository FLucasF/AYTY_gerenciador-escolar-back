package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.model.Administrador;
import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.model.Professor;
import br.com.ufpb.GerenciadorEscolar.model.Usuario;
import br.com.ufpb.GerenciadorEscolar.service.interfaces.AdministradorServiceInterface;
import br.com.ufpb.GerenciadorEscolar.service.interfaces.AlunoServiceInterface;
import br.com.ufpb.GerenciadorEscolar.service.interfaces.ProfessorServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AdministradorServiceInterface administradorService;
    @Autowired
    private ProfessorServiceInterface professorService;
    @Autowired
    private AlunoServiceInterface alunoService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<? extends Usuario> usuarioOpt = administradorService.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            usuarioOpt = professorService.findByEmail(email);
        }
        if (usuarioOpt.isEmpty()) {
            usuarioOpt = alunoService.findByEmail(email);
        }
        Usuario usuario = usuarioOpt.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        String role;
        if (usuario instanceof Administrador) {
            role = "ADMIN";
        } else if (usuario instanceof Professor) {
            role = "PROFESSOR";
        } else if (usuario instanceof Aluno) {
            role = "ALUNO";
        } else {
            role = "USER";
        }
        return new org.springframework.security.core.userdetails.User(
                usuario.getEmail(), usuario.getSenha(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }
}
