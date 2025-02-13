package br.com.ufpb.GerenciadorEscolar.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "professores")
public class Professor extends Usuario {
    private String departamento;
    private String siape;

    @JsonManagedReference
    @OneToMany(mappedBy = "professor", cascade = CascadeType.ALL)
    private List<Turma> turmas;

    public Professor() {}

    public Professor(String nome, String email, String senha, String cpf, String departamento, String siape) {
        super(nome, email, senha, cpf); // Chama o construtor da classe Usuario
        this.departamento = departamento;
        this.siape = siape;
    }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public List<Turma> getTurmas() { return turmas; }
    public void setTurmas(List<Turma> turmas) { this.turmas = turmas; }

    public String getSiape() { return siape; }
    public void setSiape(String siape) { this.siape = siape; }
}
