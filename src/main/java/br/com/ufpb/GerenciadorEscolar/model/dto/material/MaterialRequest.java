package br.com.ufpb.GerenciadorEscolar.model.dto.material;

import br.com.ufpb.GerenciadorEscolar.model.entity.TipoArquivo;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para upload de um material (arquivo) associado a uma turma e professor.")
public record MaterialRequest(

        @Schema(
                description = "Identificador da mídia (arquivo) a ser vinculado.",
                example = "abc123",
                required = true
        )
        @NotBlank(message = "O id da mídia é obrigatório")
        String arquivoId,

        @Schema(
                description = "Nome original do arquivo.",
                example = "material_de_aula.pdf",
                required = true
        )
        @NotBlank(message = "O nome do arquivo não pode estar vazio")
        String nomeArquivo,

        @Schema(
                description = "Tipo do arquivo (por exemplo, IMAGEM, DOCUMENTO).",
                example = "IMAGEM",
                required = true
        )
        @NotNull(message = "O tipo do arquivo é obrigatório")
        TipoArquivo tipoArquivo,

        @Schema(
                description = "Identificador da turma associada.",
                example = "1",
                required = true
        )
        @NotNull(message = "O id da turma é obrigatório")
        @Min(value = 1, message = "O campo turmaId deve ser maior que 0")
        Long turmaId,

        @Schema(
                description = "Identificador do professor responsável.",
                example = "2",
                required = true
        )
        @NotNull(message = "O id do professor é obrigatório")
        @Min(value = 1, message = "O campo professorId deve ser maior que 0")
        Long professorId
) {}
