package br.com.ufpb.GerenciadorEscolar.dto.material;

import br.com.ufpb.GerenciadorEscolar.model.TipoArquivo;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MaterialRequest(
        @NotBlank(message = "O id da mídia é obrigatório")
        String arquivoId,

        @NotBlank(message = "O nome do arquivo não pode estar vazio")
        String nomeArquivo,

        @NotNull(message = "O tipo do arquivo é obrigatório")
        TipoArquivo tipoArquivo,

        @NotNull(message = "O id da turma é obrigatório")
        @Min(value = 1, message = "O campo turmaId deve ser maior que 0")
        Long turmaId,

        @NotNull(message = "O id do professor é obrigatório")
        @Min(value = 1, message = "O campo professorId deve ser maior que 0")
        Long professorId
) {}
