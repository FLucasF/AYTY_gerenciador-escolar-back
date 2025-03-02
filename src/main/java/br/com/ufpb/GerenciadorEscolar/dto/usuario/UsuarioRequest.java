package br.com.ufpb.GerenciadorEscolar.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
        @NotBlank(message = "Campo nome não pode ser vazio")
        @Size(min = 3, max = 50, message = "Número de caracteres inválido")
        String nome,

        @NotBlank(message = "Campo email não pode ser vazio")
        @Size(max = 100, message = "Número de caracteres inválido")
        @Email(message = "Email inválido")
        String email,

        @NotBlank(message = "Campo senha não pode ser vazio")
        @Size(min = 8, max = 20, message = "A senha precisa ter entre 8-20 caracteres")
        String senha,

        @NotBlank(message = "Campo CPF não pode ser vazio")
        @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos numéricos")
        String cpf,

        @NotBlank(message = "Campo role não pode ser vazio (ALUNO, PROFESSOR, ADMINISTRADOR)")
        String role
) {}
