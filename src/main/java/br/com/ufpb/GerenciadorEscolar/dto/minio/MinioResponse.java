package br.com.ufpb.GerenciadorEscolar.dto.minio;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta retornada pelo MinIO após o upload do arquivo.")
public record MinioResponse(

        @Schema(description = "Identificador único do arquivo no MinIO.", example = "123")
        Long id,

        @Schema(description = "Nome do serviço que realizou o upload.", example = "gerenciadorEscolar")
        String serviceName,

        @Schema(description = "Nome original do arquivo enviado.", example = "documento.pdf")
        String fileName,

        @Schema(description = "URL assinada para acesso ao arquivo.", example = "https://minio.example.com/documento.pdf")
        String url
) {}
