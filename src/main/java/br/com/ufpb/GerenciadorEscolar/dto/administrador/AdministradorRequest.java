package br.com.ufpb.GerenciadorEscolar.dto.administrador;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para criação ou atualização de um administrador.")
public record AdministradorRequest(

        @Schema(
                description = "Nome do administrador. Deve ter entre 5 e 50 caracteres.",
                example = "João da Silva",
                required = true
        )
        @NotBlank(message = "Campo nome não pode ser vazio")
        @Size(min = 5, max = 50, message = "Número de caracteres inválido")
        String nome,

        @Schema(
                description = "Email do administrador. Máximo de 100 caracteres.",
                example = "joao@example.com",
                required = true
        )
        @NotBlank(message = "Campo email não pode ser vazio")
        @Size(max = 100, message = "Número de caracteres inválido")
        @Email(message = "Email inválido")
        String email,

        @Schema(
                description = "Senha do administrador.",
                example = "SenhaForte@123",
                required = true
        )
        String senha,

        @Schema(
                description = "CPF do administrador. Deve conter exatamente 11 dígitos numéricos.",
                example = "12345678901",
                required = true
        )
        @NotBlank(message = "Campo CPF não pode ser vazio")
        @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos numéricos")
        String cpf,

        @Schema(
                description = "Setor do administrador.",
                example = "Recursos Humanos",
                required = true
        )
        @NotBlank(message = "Campo setor não pode ser vazio")
        String setor,

        @Schema(
                description = "SIAPE do administrador. Deve conter exatamente 7 dígitos numéricos.",
                example = "1234567",
                required = true
        )
        @NotBlank(message = "Campo SIAPE não pode ser vazio")
        @Pattern(regexp = "\\d{7}", message = "O SIAPE deve conter exatamente 7 dígitos numéricos")
        String siape
) {
}
