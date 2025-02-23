// 📌 TurmaRequest.java
package br.com.ufpb.GerenciadorEscolar.dto.turma;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record TurmaRequest(
        @NotBlank(message = "O nome da turma não pode ser vazio")
        @Size(min = 3, max = 100, message = "O nome da turma deve ter entre 3 e 100 caracteres")
        String nome,

        String codigo,

        String semestre,

        @NotNull(message = "O professorId é obrigatório")
        Long professorId
) {}
