package br.com.ufpb.GerenciadorEscolar.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "alunos")
public class Aluno extends Usuario {
    private String curso;

    @ManyToMany(mappedBy = "alunos") // Relacionamento com a tabela intermediária
    private List<Turma> turmas;

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public List<Turma> getTurmas() {
        return turmas;
    }

    public void setTurmas(List<Turma> turmas) {
        this.turmas = turmas;
    }
}

