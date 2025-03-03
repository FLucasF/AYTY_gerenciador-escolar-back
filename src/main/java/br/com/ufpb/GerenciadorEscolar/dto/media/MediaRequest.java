package br.com.ufpb.GerenciadorEscolar.dto.media;

import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotBlank;

public record MediaRequest(
        @NotBlank(message = "O service name é obrigatório")
        String serviceName,

        @NotBlank(message = "O arquivo é obrigatório")
        MultipartFile file,

        @NotBlank(message = "A tag é obrigatória")
        String tag,

        @NotBlank(message = "O tipo da entidade é obrigatório")
        String entityType,

        @NotBlank(message = "O id do usuário que enviou é obrigatório")
        Long uploadedBy
) {}
