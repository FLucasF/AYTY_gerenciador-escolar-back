package br.com.ufpb.GerenciadorEscolar.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "administradores")
public class Administrador extends Usuario {
    private String setor;
    private String siape;

    public Administrador() {}

    public Administrador(String nome, String email, String senha, String cpf, String setor, String siape) {
        super(nome, email, senha, cpf); // Chama o construtor de Usuario
        this.setor = setor;
        this.siape = siape;
    }

    public String getSetor() { return setor; }
    public void setSetor(String setor) { this.setor = setor; }

    public String getSiape() { return siape; }
    public void setSiape(String siape) { this.siape = siape; }
}
