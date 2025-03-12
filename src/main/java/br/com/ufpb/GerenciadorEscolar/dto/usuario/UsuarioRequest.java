package br.com.ufpb.GerenciadorEscolar.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados do usuário para criação ou atualização.")
public record UsuarioRequest(

        @Schema(
                description = "Nome do usuário. Deve ter entre 3 e 50 caracteres.",
                example = "Ana Maria",
                required = true
        )
        @NotBlank(message = "Campo nome não pode ser vazio")
        @Size(min = 3, max = 50, message = "Número de caracteres inválido")
        String nome,

        @Schema(
                description = "Email do usuário. Máximo de 100 caracteres e formato válido.",
                example = "ana.maria@example.com",
                required = true
        )
        @NotBlank(message = "Campo email não pode ser vazio")
        @Size(max = 100, message = "Número de caracteres inválido")
        @Email(message = "Email inválido")
        String email,

        @Schema(
                description = "Senha do usuário. Deve ter pelo menos 8 caracteres, incluindo uma letra maiúscula, uma minúscula, um número e um caractere especial.",
                example = "SenhaForte@123",
                required = true
        )
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "A senha deve ter pelo menos 8 caracteres, incluindo uma letra maiúscula, uma minúscula, um número e um caractere especial."
        )
        String senha,

        @Schema(
                description = "CPF do usuário. Deve conter exatamente 11 dígitos numéricos.",
                example = "12345678901",
                required = true
        )
        @NotBlank(message = "Campo CPF não pode ser vazio")
        @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos numéricos")
        String cpf,

        @Schema(
                description = "Role do usuário (ALUNO, PROFESSOR, ADMINISTRADOR).",
                example = "ALUNO",
                required = true
        )
        @NotBlank(message = "Campo role não pode ser vazio (ALUNO, PROFESSOR, ADMINISTRADOR)")
        String role
) {}
