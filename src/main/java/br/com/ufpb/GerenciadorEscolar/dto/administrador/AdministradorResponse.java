// 📌 AdministradorResponse.java
package br.com.ufpb.GerenciadorEscolar.dto.administrador;

public record AdministradorResponse(
        Long id,
        String nome,
        String email,
        String cpf,
        String setor,
        String siape
) {}
