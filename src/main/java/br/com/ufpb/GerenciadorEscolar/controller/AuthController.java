package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorResponse;
import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoResponse;
import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorResponse;
import br.com.ufpb.GerenciadorEscolar.model.Administrador;
import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.model.Professor;
import br.com.ufpb.GerenciadorEscolar.model.Usuario;
import br.com.ufpb.GerenciadorEscolar.security.TokenProvider;
import br.com.ufpb.GerenciadorEscolar.service.AdministradorServiceInterface;
import br.com.ufpb.GerenciadorEscolar.service.AlunoServiceInterface;
import br.com.ufpb.GerenciadorEscolar.service.ProfessorServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AdministradorServiceInterface administradorService;
    private final ProfessorServiceInterface professorService;
    private final AlunoServiceInterface alunoService;
    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthController(AdministradorServiceInterface administradorService,
                          ProfessorServiceInterface professorService,
                          AlunoServiceInterface alunoService,
                          TokenProvider tokenProvider,
                          AuthenticationManager authenticationManager) {
        this.administradorService = administradorService;
        this.professorService = professorService;
        this.alunoService = alunoService;
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String senha = credentials.get("senha");

        // Busca por usuários ativos (desativados não serão retornados)
        Optional<Usuario> usuarioOpt = administradorService.findByEmail(email)
                .map(admin -> (Usuario) admin)
                .or(() -> professorService.findByEmail(email).map(prof -> (Usuario) prof))
                .or(() -> alunoService.findByEmail(email).map(aluno -> (Usuario) aluno));

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Usuário não encontrado ou inativo.");
        }

        // Autentica o usuário com as credenciais
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, senha));

        Usuario usuario = usuarioOpt.get();
        String token = tokenProvider.generateAccessToken(usuario);

        // Converte para DTO conforme o tipo de usuário
        Object usuarioResponse;
        if (usuario instanceof Administrador admin) {
            usuarioResponse = new AdministradorResponse(admin.getId(), admin.getNome(), admin.getEmail(), admin.getCpf(), admin.getSetor(), admin.getSiape());
        } else if (usuario instanceof Professor prof) {
            usuarioResponse = new ProfessorResponse(prof.getId(), prof.getNome(), prof.getEmail(), prof.getCpf(), prof.getDepartamento(), prof.getSiape());
        } else if (usuario instanceof Aluno aluno) {
            usuarioResponse = new AlunoResponse(aluno.getId(), aluno.getNome(), aluno.getEmail(), aluno.getCpf(), aluno.getCurso());
        } else {
            return ResponseEntity.status(500).body("Tipo de usuário desconhecido.");
        }

        return ResponseEntity.ok(Map.of(
                "accessToken", token,
                "usuario", usuarioResponse
        ));
    }
}
