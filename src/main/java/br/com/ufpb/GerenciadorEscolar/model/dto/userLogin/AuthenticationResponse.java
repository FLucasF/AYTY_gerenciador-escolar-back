package br.com.ufpb.GerenciadorEscolar.model.dto.userLogin;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta de autenticação contendo o token de acesso, o refresh token, a role do usuário e o ID")
public record AuthenticationResponse(
        @Schema(description = "Token de acesso JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String accessToken,

        @Schema(description = "ID do usuário autenticado", example = "123")
        Long userId,

        @Schema(description = "Role do usuário", example = "ADMIN")
        String role
) {}
