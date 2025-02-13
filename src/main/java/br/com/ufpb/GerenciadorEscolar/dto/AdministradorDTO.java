package br.com.ufpb.GerenciadorEscolar.dto;

import br.com.ufpb.GerenciadorEscolar.model.Administrador;

public class AdministradorDTO extends UsuarioDTO {
    private String setor;

    public AdministradorDTO(Long id, String nome, String email, String setor) {
        super(id, nome, email); // Chama o construtor de UsuarioDTO
        this.setor = setor;
    }

    public String getSetor() {
        return setor;
    }
}
