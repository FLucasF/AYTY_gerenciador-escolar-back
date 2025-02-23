package br.com.ufpb.GerenciadorEscolar.dto.material;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MaterialRequest(
        @NotBlank(message = "Nome do arquivo não pode ser vazio")
        String nomeArquivo,

        @NotBlank(message = "URL do arquivo não pode ser vazio")
        String urlArquivo,

        @NotNull(message = "TurmaId é obrigatório")
        Long turmaId,

        @NotNull(message = "ProfessorId é obrigatório")
        Long professorId
) {}
