package br.com.ufpb.GerenciadorEscolar.dto.usuario;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        String cpf,
        String role,
        String curso,         // Para aluno
        String setor,         // Para administrador
        String departamento   // Para professor
) {}
