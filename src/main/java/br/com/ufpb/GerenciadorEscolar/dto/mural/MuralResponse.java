package br.com.ufpb.GerenciadorEscolar.dto.mural;

import java.time.LocalDateTime;

public record MuralResponse(
        Long id,
        String titulo,
        String conteudo,
        LocalDateTime dataCriacao,
        Long turmaId,
        Long professorId
) {}
