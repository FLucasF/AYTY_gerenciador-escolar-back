package br.com.ufpb.GerenciadorEscolar.dto.media;

public record MediaResponse(
        Long id,
        String serviceName,
        String mediaType,
        String entityType,
        Long uploadedBy,
        String fileName,
        String tag,
        Boolean active
) {}
