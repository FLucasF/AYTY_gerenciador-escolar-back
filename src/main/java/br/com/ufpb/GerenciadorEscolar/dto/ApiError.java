package br.com.ufpb.GerenciadorEscolar.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estrutura padrão para erros retornados pela API, seguindo o padrão RFC 7807.")
public record ApiError(

        @Schema(
                description = "Código HTTP do erro.",
                example = "404"
        )
        int status,

        @Schema(
                description = "Título resumido do erro.",
                example = "Recurso não encontrado"
        )
        String title,

        @Schema(
                description = "Mensagem detalhada que explica o erro, sem expor informações internas sensíveis.",
                example = "O recurso solicitado não foi encontrado."
        )
        String detail,

        @Schema(
                description = "URL que aponta para uma página com informações detalhadas sobre esse tipo de erro.",
                example = "https://api.gerenciadordeturmas.com/probs/recurso-not-found"
        )
        String type,

        @Schema(
                description = "URI da requisição que gerou o erro.",
                example = "/turmas/1"
        )
        String instance
) {}
