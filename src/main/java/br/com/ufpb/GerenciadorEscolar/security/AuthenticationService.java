package br.com.ufpb.GerenciadorEscolar.security;

import br.com.ufpb.GerenciadorEscolar.model.Administrador;
import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.model.Professor;
import br.com.ufpb.GerenciadorEscolar.model.Usuario;
import br.com.ufpb.GerenciadorEscolar.repository.AdministradorRepository;
import br.com.ufpb.GerenciadorEscolar.repository.AlunoRepository;
import br.com.ufpb.GerenciadorEscolar.repository.ProfessorRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthenticationService implements UserDetailsService {

    private final AdministradorRepository administradorRepository;
    private final ProfessorRepository professorRepository;
    private final AlunoRepository alunoRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(
            AdministradorRepository administradorRepository,
            ProfessorRepository professorRepository,
            AlunoRepository alunoRepository,
            PasswordEncoder passwordEncoder) {
        this.administradorRepository = administradorRepository;
        this.professorRepository = professorRepository;
        this.alunoRepository = alunoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Usuario> usuario = buscarUsuarioPorEmail(email);

        if (usuario.isEmpty()) {
            throw new UsernameNotFoundException("Usuário não encontrado: " + email);
        }

        return new User(
                usuario.get().getEmail(),
                usuario.get().getSenha(),
                List.of(() -> usuario.get().getRole()) // Define a role corretamente
        );
    }

    public Optional<Usuario> buscarUsuarioPorEmail(String email) {
        return administradorRepository.findByEmailAndAtivoTrue(email).map(admin -> (Usuario) admin)
                .or(() -> professorRepository.findByEmailAndAtivoTrue(email).map(prof -> (Usuario) prof))
                .or(() -> alunoRepository.findByEmailAndAtivoTrue(email).map(aluno -> (Usuario) aluno));
    }


    public String autenticarUsuario(String email, String senha, JwtUtil jwtUtil) {
        Optional<Usuario> usuario = buscarUsuarioPorEmail(email);

        if (usuario.isEmpty() || !passwordEncoder.matches(senha, usuario.get().getSenha())) {
            throw new UsernameNotFoundException("Credenciais inválidas!");
        }

        UserDetails userDetails = loadUserByUsername(email);
        return jwtUtil.generateToken(userDetails, usuario.get().getRole());
    }
}
