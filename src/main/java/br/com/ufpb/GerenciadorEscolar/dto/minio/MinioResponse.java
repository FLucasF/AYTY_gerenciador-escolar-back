package br.com.ufpb.GerenciadorEscolar.dto.minio;

public record MinioResponse(
        Long id,
        String serviceName,
        String fileName,
        String url
) {}
