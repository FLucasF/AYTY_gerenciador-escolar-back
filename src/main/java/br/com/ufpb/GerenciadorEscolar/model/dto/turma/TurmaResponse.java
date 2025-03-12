package br.com.ufpb.GerenciadorEscolar.model.dto.turma;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados retornados sobre uma turma.")
public record TurmaResponse(

        @Schema(description = "Identificador único da turma.", example = "1")
        Long id,

        @Schema(description = "Nome da turma.", example = "Turma A")
        String nome,

        @Schema(description = "Código identificador da turma.", example = "TURMA-A-2025")
        String codigo,

        @Schema(description = "Semestre em que a turma está ativa.", example = "2025.1")
        String semestre,

        @Schema(description = "Identificador do professor responsável pela turma.", example = "1")
        Long professorId
) {}
