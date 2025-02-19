package br.com.ufpb.GerenciadorEscolar.dto;

import br.com.ufpb.GerenciadorEscolar.model.Turma;

public class TurmaDTO {
    private Long id;
    private String nome;
    private String codigo;
    private String semestre;
    private Long professorId;
    private String professorNome;

    public TurmaDTO(Turma turma) {
        this.id = turma.getId();
        this.nome = turma.getNome();
        this.codigo = turma.getCodigo();
        this.semestre = turma.getSemestre();
        this.professorId = (turma.getProfessor() != null) ? turma.getProfessor().getId() : null;
        this.professorNome = (turma.getProfessor() != null) ? turma.getProfessor().getNome() : "Sem professor";
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getCodigo() { return codigo; }
    public String getSemestre() { return semestre; }
    public Long getProfessorId() { return professorId; }
    public String getProfessorNome() { return professorNome; }
}
