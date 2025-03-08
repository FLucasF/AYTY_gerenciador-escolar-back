package br.com.ufpb.GerenciadorEscolar.dto.mural;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record MuralRequest(
        @NotBlank(message = "O título não pode ser vazio")
        @Size(min = 3, max = 100, message = "O título deve ter entre 3 e 100 caracteres")
        String titulo,

        @NotBlank(message = "O conteúdo não pode ser vazio")
        String conteudo,

        @NotNull(message = "O campo turmaId é obrigatório")
        @Min(value = 1, message = "O campo turmaId deve ser maior que 0")
        Long turmaId,

        @NotNull(message = "O campo professorId é obrigatório")
        @Min(value = 1, message = "O campo professorId deve ser maior que 0")
        Long professorId,

        List<Long> materiaisIds // Opcional: pode ser null ou vazio
) {}
