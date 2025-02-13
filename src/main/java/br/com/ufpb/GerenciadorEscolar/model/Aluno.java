package br.com.ufpb.GerenciadorEscolar.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.List;
import jakarta.persistence.ManyToMany;

@Entity
@Table(name = "alunos")
public class Aluno extends Usuario {

    private String curso;

    @ManyToMany(mappedBy = "alunos")
    private List<Turma> turmas;

    public Aluno() {}

    public Aluno(String nome, String email, String senha, String cpf, String curso) {
        super(nome, email, senha, cpf);
        this.curso = curso;
    }

    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }

    public List<Turma> getTurmas() { return turmas; }
    public void setTurmas(List<Turma> turmas) { this.turmas = turmas; }
}
