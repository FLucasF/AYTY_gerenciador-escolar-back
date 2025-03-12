package br.com.ufpb.GerenciadorEscolar.model.dto.aluno;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados retornados sobre o aluno.")
public record AlunoResponse(
        @Schema(description = "Identificador único do aluno.", example = "1")
        Long id,

        @Schema(description = "Nome do aluno.", example = "Maria da Silva")
        String nome,

        @Schema(description = "Email do aluno.", example = "maria@example.com")
        String email,

        @Schema(description = "CPF do aluno.", example = "12345678901")
        String cpf,

        @Schema(description = "Curso em que o aluno está matriculado.", example = "Engenharia de Software")
        String curso
) {}
