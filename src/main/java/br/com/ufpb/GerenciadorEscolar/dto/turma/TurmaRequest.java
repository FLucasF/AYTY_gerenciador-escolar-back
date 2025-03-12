package br.com.ufpb.GerenciadorEscolar.dto.turma;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados necessários para criar ou atualizar uma turma.")
public record TurmaRequest(

        @Schema(
                description = "Nome da turma. Deve ter entre 3 e 50 caracteres.",
                example = "Turma A",
                required = true
        )
        @NotBlank(message = "O nome da turma não pode ser vazio")
        @Size(min = 3, max = 50, message = "O nome da turma deve ter entre 3 e 50 caracteres")
        String nome,

        @Schema(
                description = "Código identificador da turma.",
                example = "TURMA-A-2025",
                required = true
        )
        @NotBlank(message = "O código da turma é obrigatório")
        @Min(value = 1, message = "O campo codigo deve ser maior que 0")
        String codigo,

        @Schema(
                description = "Semestre em que a turma está ativa.",
                example = "2025.1",
                required = true
        )
        @NotBlank(message = "O semestre da turma é obrigatório")
        String semestre,

        @Schema(
                description = "Identificador do professor responsável pela turma.",
                example = "1",
                required = true
        )
        @NotNull(message = "O professorId é obrigatório")
        @Min(value = 1, message = "O campo professorId deve ser maior que 0")
        Long professorId
) {}
