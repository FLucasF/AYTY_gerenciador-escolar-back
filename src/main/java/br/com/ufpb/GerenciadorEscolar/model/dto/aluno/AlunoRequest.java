package br.com.ufpb.GerenciadorEscolar.model.dto.aluno;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados do aluno para criação ou atualização.")
public record AlunoRequest(

        @Schema(
                description = "Nome do aluno. Deve ter entre 3 e 50 caracteres.",
                example = "Maria da Silva",
                required = true
        )
        @NotBlank(message = "Campo nome não pode ser vazio")
        @Size(min = 3, max = 50, message = "Número de caracteres inválido")
        String nome,

        @Schema(
                description = "Email do aluno. Máximo 100 caracteres e formato válido.",
                example = "maria@example.com",
                required = true
        )
        @NotBlank(message = "Campo email não pode ser vazio")
        @Size(max = 100, message = "Número de caracteres inválido")
        @Email(message = "Email inválido")
        String email,

        @Schema(
                description = "Senha do aluno.",
                example = "SenhaForte@123",
                required = false
        )
        String senha,

        @Schema(
                description = "CPF do aluno. Deve conter exatamente 11 dígitos numéricos.",
                example = "12345678901",
                required = true
        )
        @NotBlank(message = "Campo CPF não pode ser vazio")
        @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos numéricos")
        String cpf,

        @Schema(
                description = "Curso no qual o aluno está matriculado.",
                example = "Engenharia de Software",
                required = true
        )
        @NotBlank(message = "Campo curso não pode ser vazio")
        String curso
) {}
