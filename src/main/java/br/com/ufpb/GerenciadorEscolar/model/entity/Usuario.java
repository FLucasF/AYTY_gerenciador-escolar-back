package br.com.ufpb.GerenciadorEscolar.model.entity;

import br.com.ufpb.GerenciadorEscolar.util.CryptoConverter;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "usuarios")
public abstract class Usuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Convert(converter = CryptoConverter.class)
    private String email;

    private String senha;

    @Convert(converter = CryptoConverter.class)
    private String cpf;

    private boolean ativo = true;

    @Column(nullable = false)
    private String role;

    public Usuario() {}

    public Usuario(String nome, String email, String senha, String cpf, String role) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.cpf = cpf;
        this.role = role;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
