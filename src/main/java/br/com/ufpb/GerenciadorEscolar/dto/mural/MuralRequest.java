package br.com.ufpb.GerenciadorEscolar.dto.mural;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MuralRequest(
        @NotBlank(message = "Título não pode ser vazio")
        @Size(min = 3, max = 100, message = "O título deve ter entre 3 e 100 caracteres")
        String titulo,

        @NotBlank(message = "Conteúdo não pode ser vazio")
        String conteudo,

        Long turmaId,
        Long professorId
) {}
