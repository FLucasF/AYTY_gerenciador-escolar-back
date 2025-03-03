package br.com.ufpb.GerenciadorEscolar.dto.material;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MaterialRequest(
        @NotNull(message = "O id da mídia é obrigatório")
        Long mediaId,  // ✅ Esse ID vem da API de Mídia

        @NotBlank(message = "O nome do arquivo não pode ser vazio")
        String fileName,

        @NotNull(message = "O id da turma é obrigatório")
        Long turmaId,

        @NotNull(message = "O id do professor é obrigatório")
        Long professorId,

        @NotBlank(message = "O service name é obrigatório")
        String serviceName,

        @NotBlank(message = "A tag é obrigatória")
        String tag,

        @NotNull(message = "O id do usuário que enviou é obrigatório")
        Long uploadedBy
) {}
