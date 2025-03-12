package br.com.ufpb.GerenciadorEscolar.dto.userLogin;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta de autenticação contendo o token de acesso, o refresh token, a role do usuário e o ID")
public record AuthenticationResponse(
        @Schema(description = "Token de acesso JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String accessToken,

        @Schema(description = "Refresh token", example = "e4f9a7b2-5d4c-4e67-8e9f-3b9d1a6d5e44")
        String refreshToken,

        @Schema(description = "ID do usuário autenticado", example = "123")
        Long userId,

        @Schema(description = "Role do usuário", example = "ADMIN")
        String role
) {}
