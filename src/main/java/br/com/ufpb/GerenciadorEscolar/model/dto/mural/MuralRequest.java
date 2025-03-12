package br.com.ufpb.GerenciadorEscolar.model.dto.mural;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Requisição para criação ou atualização de uma postagem no mural.")
public record MuralRequest(

        @Schema(
                description = "Título da postagem. Deve ter entre 3 e 100 caracteres.",
                example = "Aviso importante",
                required = true
        )
        @NotBlank(message = "O título não pode ser vazio")
        @Size(min = 3, max = 100, message = "O título deve ter entre 3 e 100 caracteres")
        String titulo,

        @Schema(
                description = "Conteúdo da postagem.",
                example = "Atenção, haverá manutenção no sistema amanhã.",
                required = true
        )
        @NotBlank(message = "O conteúdo não pode ser vazio")
        String conteudo,

        @Schema(
                description = "Identificador da turma associada à postagem.",
                example = "1",
                required = true
        )
        @NotNull(message = "O campo turmaId é obrigatório")
        @Min(value = 1, message = "O campo turmaId deve ser maior que 0")
        Long turmaId,

        @Schema(
                description = "Identificador do professor que realizou a postagem.",
                example = "2",
                required = true
        )
        @NotNull(message = "O campo professorId é obrigatório")
        @Min(value = 1, message = "O campo professorId deve ser maior que 0")
        Long professorId,

        @Schema(
                description = "Lista de identificadores dos materiais associados à postagem. Opcional.",
                example = "[10, 20]",
                required = false
        )
        List<Long> materiaisIds
) {}
