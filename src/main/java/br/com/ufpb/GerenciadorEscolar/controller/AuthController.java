package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.model.dto.userLogin.AuthenticationRequest;
import br.com.ufpb.GerenciadorEscolar.model.dto.userLogin.AuthenticationResponse;
import br.com.ufpb.GerenciadorEscolar.security.jwt.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse authResponse = authenticationService.login(request);
        return ResponseEntity.ok(authResponse); // Retorna apenas o token JWT na resposta
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        authenticationService.logout(); // Apenas realiza o logout no backend
        return ResponseEntity.noContent().build();
    }
}
