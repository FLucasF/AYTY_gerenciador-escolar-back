package br.com.ufpb.GerenciadorEscolar.model.entity;

import br.com.ufpb.GerenciadorEscolar.util.CryptoConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class UserLogin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = CryptoConverter.class)
    private String email;

    @Convert(converter = CryptoConverter.class)
    private String senha;

    @NotNull
    private boolean ativo = true;

    @OneToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    @Convert(converter = CryptoConverter.class)
    private Usuario usuario;

    public UserLogin() {}

    public UserLogin(String email, String senha, Usuario usuario) {
        this.email = email;
        this.senha = senha;
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
