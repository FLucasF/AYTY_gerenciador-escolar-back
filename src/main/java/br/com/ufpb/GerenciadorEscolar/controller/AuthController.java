package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.model.dto.ApiError;
import br.com.ufpb.GerenciadorEscolar.model.dto.userLogin.AuthenticationRequest;
import br.com.ufpb.GerenciadorEscolar.model.dto.userLogin.AuthenticationResponse;
import br.com.ufpb.GerenciadorEscolar.security.jwt.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Operation(
            summary = "Realiza o login do usuário",
            description = "Autentica o usuário com as credenciais fornecidas (email e senha), gerando um token JWT para uso em requisições subsequentes."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso. Retorna o token JWT gerado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Credenciais inválidas ou dados errados.",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse authResponse = authenticationService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(authResponse); // Retorna o token JWT na resposta
    }

    @Operation(
            summary = "Desloga o usuário",
            description = "Realiza o logout do usuário, invalidando o token JWT no backend. Não remove o token do lado do cliente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Logout realizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro ao realizar o logout.",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        authenticationService.logout(); // Apenas realiza o logout no backend
        return ResponseEntity.noContent().build();
    }
}
