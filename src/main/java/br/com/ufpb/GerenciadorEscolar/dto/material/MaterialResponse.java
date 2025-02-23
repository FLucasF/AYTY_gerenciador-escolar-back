package br.com.ufpb.GerenciadorEscolar.dto.material;

public record MaterialResponse(
        Long id,
        String nomeArquivo,
        String urlArquivo,
        Long turmaId,
        Long professorId
) {}
