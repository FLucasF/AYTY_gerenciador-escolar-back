package br.com.ufpb.GerenciadorEscolar.dto.administrador;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta com os dados do administrador.")
public record AdministradorResponse(

        @Schema(description = "Identificador único do administrador.", example = "1")
        Long id,

        @Schema(description = "Nome do administrador.", example = "João da Silva")
        String nome,

        @Schema(description = "Email do administrador.", example = "joao@example.com")
        String email,

        @Schema(description = "CPF do administrador.", example = "12345678901")
        String cpf,

        @Schema(description = "Setor do administrador.", example = "Recursos Humanos")
        String setor,

        @Schema(description = "SIAPE do administrador.", example = "1234567")
        String siape
) {}
