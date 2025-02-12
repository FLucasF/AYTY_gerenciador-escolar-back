package br.com.ufpb.GerenciadorEscolar.model;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "professores")
public class Professor extends Usuario {
    private String departamento;

    @OneToMany(mappedBy = "professor") // Professor ministra várias turmas
    private List<Turma> turmas;

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public List<Turma> getTurmas() {
        return turmas;
    }

    public void setTurmas(List<Turma> turmas) {
        this.turmas = turmas;
    }
}

