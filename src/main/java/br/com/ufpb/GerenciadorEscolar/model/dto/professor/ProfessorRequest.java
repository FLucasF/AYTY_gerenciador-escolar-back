package br.com.ufpb.GerenciadorEscolar.model.dto.professor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados do professor para criação ou atualização.")
public record ProfessorRequest(

        @Schema(
                description = "Nome do professor. Deve ter entre 3 e 50 caracteres.",
                example = "Carlos Eduardo",
                required = true
        )
        @NotBlank(message = "Campo nome não pode ser vazio")
        @Size(min = 3, max = 50, message = "Número de caracteres inválido")
        String nome,

        @Schema(
                description = "Email do professor. Máximo de 100 caracteres e formato válido.",
                example = "carlos.eduardo@example.com",
                required = true
        )
        @NotBlank(message = "Campo email não pode ser vazio")
        @Size(max = 100, message = "Número de caracteres inválido")
        @Email(message = "Email inválido")
        String email,

        @Schema(
                description = "Senha do professor.",
                example = "SenhaForte@123",
                required = false
        )
        String senha,

        @Schema(
                description = "CPF do professor. Deve conter exatamente 11 dígitos numéricos.",
                example = "12345678901",
                required = true
        )
        @NotBlank(message = "Campo CPF não pode ser vazio")
        @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos numéricos")
        String cpf,

        @Schema(
                description = "Departamento ao qual o professor pertence.",
                example = "Ciências da Computação",
                required = true
        )
        @NotBlank(message = "Campo departamento não pode ser vazio")
        String departamento,

        @Schema(
                description = "SIAPE do professor. Deve conter exatamente 7 dígitos numéricos.",
                example = "1234567",
                required = true
        )
        @NotBlank(message = "Campo SIAPE não pode ser vazio")
        @Pattern(regexp = "\\d{7}", message = "O SIAPE deve conter exatamente 7 dígitos numéricos")
        String siape
) {}
