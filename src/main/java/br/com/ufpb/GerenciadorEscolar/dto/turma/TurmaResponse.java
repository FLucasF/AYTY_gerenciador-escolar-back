// 📌 TurmaResponse.java
package br.com.ufpb.GerenciadorEscolar.dto.turma;

public record TurmaResponse(
        Long id,
        String nome,
        String codigo,
        String semestre
) {}
