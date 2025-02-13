package br.com.ufpb.GerenciadorEscolar.dto;

import br.com.ufpb.GerenciadorEscolar.model.Turma;

import java.util.List;
import java.util.stream.Collectors;

public class TurmaDTO {
    private Long id;
    private String nome;
    private String codigo;
    private String semestre;
    private String professor;
    private List<String> alunos;

    public TurmaDTO(Turma turma) {
        this.id = turma.getId();
        this.nome = turma.getNome();
        this.codigo = turma.getCodigo();
        this.semestre = turma.getSemestre();
        this.professor = turma.getProfessor() != null ? turma.getProfessor().getNome() : "Sem Professor";
        this.alunos = turma.getAlunos() != null ? turma.getAlunos().stream()
                .map(aluno -> aluno.getNome()) // Pegando apenas o nome do aluno
                .collect(Collectors.toList()) : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public String getProfessor() {
        return professor;
    }

    public List<String> getAlunos() {
        return alunos;
    }
}
