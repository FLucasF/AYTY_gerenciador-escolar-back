package br.com.ufpb.GerenciadorEscolar.dto.mural;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MuralResponse(
        Long id,
        String titulo,
        String conteudo,
        LocalDateTime dataCriacao,
        Long turmaId,
        Long professorId,
        String imagemUrl  // URL assinada da imagem, se houver
) {}
