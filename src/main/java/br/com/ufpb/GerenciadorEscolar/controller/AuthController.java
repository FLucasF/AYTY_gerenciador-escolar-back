package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorResponse;
import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoResponse;
import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorResponse;
import br.com.ufpb.GerenciadorEscolar.model.Administrador;
import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.model.Professor;
import br.com.ufpb.GerenciadorEscolar.model.Usuario;
import br.com.ufpb.GerenciadorEscolar.service.AdministradorServiceInterface;
import br.com.ufpb.GerenciadorEscolar.service.AlunoServiceInterface;
import br.com.ufpb.GerenciadorEscolar.service.ProfessorServiceInterface;
import br.com.ufpb.GerenciadorEscolar.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AdministradorServiceInterface administradorService;
    private final ProfessorServiceInterface professorService;
    private final AlunoServiceInterface alunoService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            AdministradorServiceInterface administradorService,
            ProfessorServiceInterface professorService,
            AlunoServiceInterface alunoService,
            JwtUtil jwtUtil,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder) {
        this.administradorService = administradorService;
        this.professorService = professorService;
        this.alunoService = alunoService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String senha = credentials.get("senha");

        Optional<Usuario> usuarioOpt = buscarUsuarioPorEmail(email);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Usuário não encontrado ou inativo.");
        }

        Usuario usuario = usuarioOpt.get();

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            return ResponseEntity.status(401).body("Senha incorreta.");
        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, senha));

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                usuario.getEmail(),
                usuario.getSenha(),
                List.of(new SimpleGrantedAuthority(usuario.getRole()))
        );

        String token = jwtUtil.generateToken(userDetails);
        Object usuarioResponse = converterParaResponse(usuario);

        return ResponseEntity.ok(Map.of(
                "accessToken", token,
                "role", usuario.getRole(),
                "usuario", usuarioResponse
        ));
    }



    private Optional<Usuario> buscarUsuarioPorEmail(String email) {
        return administradorService.findByEmail(email).map(admin -> (Usuario) admin)
                .or(() -> professorService.findByEmail(email).map(prof -> (Usuario) prof))
                .or(() -> alunoService.findByEmail(email).map(aluno -> (Usuario) aluno));
    }

    private Object converterParaResponse(Usuario usuario) {
        if (usuario instanceof Administrador admin) {
            return new AdministradorResponse(
                    admin.getId(), admin.getNome(), admin.getEmail(), admin.getCpf(), admin.getSetor(), admin.getSiape());
        } else if (usuario instanceof Professor prof) {
            return new ProfessorResponse(
                    prof.getId(), prof.getNome(), prof.getEmail(), prof.getCpf(), prof.getDepartamento(), prof.getSiape());
        } else if (usuario instanceof Aluno aluno) {
            return new AlunoResponse(
                    aluno.getId(), aluno.getNome(), aluno.getEmail(), aluno.getCpf(), aluno.getCurso());
        } else {
            throw new IllegalStateException("Tipo de usuário desconhecido.");
        }
    }
}
