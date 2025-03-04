package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.model.Usuario;
import br.com.ufpb.GerenciadorEscolar.security.AuthenticationService;
import br.com.ufpb.GerenciadorEscolar.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationService authenticationService, JwtUtil jwtUtil) {
        this.authenticationService = authenticationService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String senha = credentials.get("senha");

        // Autenticar usuário e gerar token
        String token = authenticationService.autenticarUsuario(email, senha, jwtUtil);

        // Buscar o usuário autenticado para obter o ID
        Optional<Usuario> usuarioOpt = authenticationService.buscarUsuarioPorEmail(email);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Usuário não encontrado"));
        }

        Usuario usuario = usuarioOpt.get();

        return ResponseEntity.ok(Map.of(
                "accessToken", token,
                "role", jwtUtil.extractRole(token).orElse("UNKNOWN"),
                "email", email,
                "id", usuario.getId() // Retorna o ID do usuário
        ));
    }
}
