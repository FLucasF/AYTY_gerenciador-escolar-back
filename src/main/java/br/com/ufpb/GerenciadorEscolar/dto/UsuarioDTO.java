package br.com.ufpb.GerenciadorEscolar.dto;

import br.com.ufpb.GerenciadorEscolar.model.Usuario;

public abstract class UsuarioDTO {
    private Long id;
    private String nome;
    private String email;

    public UsuarioDTO(Long id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
}
