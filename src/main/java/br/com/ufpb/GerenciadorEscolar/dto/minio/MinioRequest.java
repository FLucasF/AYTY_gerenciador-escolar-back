package br.com.ufpb.GerenciadorEscolar.dto.minio;

public record MinioRequest(
        Object file, // Pode ser um ByteArrayResource ou outro tipo, conforme a necessidade.

        String uploadedBy,

        String serviceName,

        String entityId
) {}
