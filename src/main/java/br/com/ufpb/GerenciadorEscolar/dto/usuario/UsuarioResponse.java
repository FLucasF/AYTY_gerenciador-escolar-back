package br.com.ufpb.GerenciadorEscolar.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados retornados sobre o usuário.")
public record UsuarioResponse(

        @Schema(description = "Identificador único do usuário.", example = "1")
        Long id,

        @Schema(description = "Nome do usuário.", example = "Ana Maria")
        String nome,

        @Schema(description = "Email do usuário.", example = "ana.maria@example.com")
        String email,

        @Schema(description = "CPF do usuário.", example = "12345678901")
        String cpf,

        @Schema(description = "Tipo do usuário.", example = "ALUNO")
        String tipo,

        @Schema(description = "Curso associado ao usuário (se aplicável).", example = "Engenharia de Software", nullable = true)
        String curso,

        @Schema(description = "Setor associado ao usuário (se aplicável).", example = "Recursos Humanos", nullable = true)
        String setor,

        @Schema(description = "Departamento associado ao usuário (se aplicável).", example = "Tecnologia", nullable = true)
        String departamento
) {}
