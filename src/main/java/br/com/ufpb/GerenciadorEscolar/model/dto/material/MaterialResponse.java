package br.com.ufpb.GerenciadorEscolar.model.dto.material;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados retornados sobre o material (arquivo) associado a uma turma e a um professor.")
public record MaterialResponse(

        @Schema(
                description = "Identificador único do material.",
                example = "1"
        )
        Long id,

        @Schema(
                description = "Nome original do arquivo.",
                example = "material_de_aula.pdf"
        )
        String nomeArquivo,

        @Schema(
                description = "URL assinada ou de acesso ao arquivo.",
                example = "https://api.gerenciadordeturmas.com/files/material_de_aula.pdf"
        )
        String urlArquivo,

        @Schema(
                description = "Identificador da turma associada ao material.",
                example = "1"
        )
        Long turmaId,

        @Schema(
                description = "Identificador do professor associado ao material.",
                example = "2"
        )
        Long professorId
) {}
