package br.com.ufpb.GerenciadorEscolar.model.dto.professor;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta contendo os dados do professor.")
public record ProfessorResponse(
        @Schema(description = "Identificador único do professor.", example = "1")
        Long id,

        @Schema(description = "Nome do professor.", example = "Carlos Eduardo")
        String nome,

        @Schema(description = "Email do professor.", example = "carlos.eduardo@example.com")
        String email,

        @Schema(description = "CPF do professor.", example = "12345678901")
        String cpf,

        @Schema(description = "Departamento do professor.", example = "Ciências da Computação")
        String departamento,

        @Schema(description = "SIAPE do professor.", example = "1234567")
        String siape
) {}
