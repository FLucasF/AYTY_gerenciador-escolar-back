package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.model.Usuario;
import br.com.ufpb.GerenciadorEscolar.security.TokenProvider;
import br.com.ufpb.GerenciadorEscolar.service.AdministradorServiceImpl;
import br.com.ufpb.GerenciadorEscolar.service.AlunoServiceImpl;
import br.com.ufpb.GerenciadorEscolar.service.ProfessorServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AdministradorServiceImpl administradorService;
    private final ProfessorServiceImpl professorService;
    private final AlunoServiceImpl alunoService;
    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthController(AdministradorServiceImpl administradorService,
                          ProfessorServiceImpl professorService,
                          AlunoServiceImpl alunoService,
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

        // Autentica as credenciais usando o AuthenticationManager
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, senha)
        );

        // Tenta encontrar o usuário nos serviços correspondentes
        Optional<? extends Usuario> usuarioOpt = administradorService.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            usuarioOpt = professorService.findByEmail(email);
        }
        if (usuarioOpt.isEmpty()) {
            usuarioOpt = alunoService.findByEmail(email);
        }

        Usuario usuario = usuarioOpt.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Gera o token JWT com base no usuário encontrado
        String token = tokenProvider.generateToken(usuario);
        return ResponseEntity.ok(Map.of("token", token, "role", usuario.getClass().getSimpleName()));
    }
}
