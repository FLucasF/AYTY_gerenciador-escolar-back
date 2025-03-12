package br.com.ufpb.GerenciadorEscolar.model.dto.mural;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Resposta retornada para uma postagem no mural.")
public record MuralResponse(

        @Schema(description = "Identificador único da postagem.", example = "1")
        Long id,

        @Schema(description = "Título da postagem.", example = "Aviso de manutenção")
        String titulo,

        @Schema(description = "Conteúdo da postagem.", example = "Haverá manutenção no sistema amanhã das 18h às 20h.")
        String conteudo,

        @Schema(description = "Data e hora de criação da postagem.", example = "2025-03-09T15:30:00")
        LocalDateTime dataCriacao,

        @Schema(description = "Identificador da turma associada à postagem.", example = "10")
        Long turmaId,

        @Schema(description = "Identificador do professor que criou a postagem.", example = "5")
        Long professorId,

        @Schema(description = "URL assinada da imagem associada à postagem, se houver.", example = "https://api.gerenciadordeturmas.com/media/assinada.jpg")
        String imagemUrl
) {}
