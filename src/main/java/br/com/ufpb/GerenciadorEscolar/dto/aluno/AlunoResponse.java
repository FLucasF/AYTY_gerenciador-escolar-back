// 📌 AlunoResponse.java
package br.com.ufpb.GerenciadorEscolar.dto.aluno;

public record AlunoResponse(
        Long id,
        String nome,
        String email,
        String cpf,
        String curso
) {}
