package br.com.ufpb.GerenciadorEscolar.dto.professor;

public record ProfessorResponse(
        Long id,
        String nome,
        String email,
        String cpf,
        String departamento,
        String siape
) {}
