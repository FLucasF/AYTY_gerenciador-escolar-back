package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.model.Usuario;
import br.com.ufpb.GerenciadorEscolar.security.AuthenticationService;
import br.com.ufpb.GerenciadorEscolar.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthenticationService authenticationService, JwtUtil jwtUtil) {
        this.authenticationService = authenticationService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String senha = credentials.get("senha");

        try {
            logger.debug("Iniciando processo de login para o email: {}", email);

            // Autenticar o usuário e gerar o token
            String token = authenticationService.loginUsuario(email, senha);
            logger.info("Usuário autenticado com sucesso, token gerado.");

            // Buscar o usuário diretamente
            Usuario usuario = authenticationService.buscarUsuarioPorEmail(email);
            logger.info("Usuário encontrado: {} com id: {}", usuario.getEmail(), usuario.getId());

            // Retorna a resposta com o token, role e ID do usuário
            return ResponseEntity.ok(Map.of(
                    "accessToken", token,
                    "role", jwtUtil.extractRole(token).orElse("UNKNOWN"),
                    "email", email,
                    "id", usuario.getId()
            ));

        } catch (UsernameNotFoundException e) {
            logger.error("Credenciais inválidas para o usuário: {}", email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Credenciais inválidas"));
        } catch (Exception e) {
            logger.error("Erro inesperado ao tentar autenticar usuário: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Erro interno no servidor"));
        }
    }
}
