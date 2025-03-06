// 📌 AdministradorRequest.java
package br.com.ufpb.GerenciadorEscolar.dto.administrador;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public record AdministradorRequest(
        @NotBlank(message = "Campo nome não pode ser vazio")
        @Size(min = 5, max = 50, message = "Número de caracteres inválido")
        String nome,

        @NotBlank(message = "Campo email não pode ser vazio")
        @Size(max = 100, message = "Número de caracteres inválido")
        @Email(message = "Email inválido")
        String email,

        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "A senha deve ter pelo menos 8 caracteres, incluindo uma letra maiúscula, uma minúscula, um número e um caractere especial."
        )
        String senha,


                @NotBlank(message = "Campo CPF não pode ser vazio")
        @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos numéricos")
        String cpf,

        @NotBlank(message = "Campo setor não pode ser vazio")
        String setor,

        @NotBlank(message = "Campo SIAPE não pode ser vazio")
        @Pattern(regexp = "\\d{7}", message = "O SIAPE deve conter exatamente 7 dígitos numéricos")
        String siape
) {}
