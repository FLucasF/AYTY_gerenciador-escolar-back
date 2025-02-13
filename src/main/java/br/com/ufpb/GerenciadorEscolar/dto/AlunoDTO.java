package br.com.ufpb.GerenciadorEscolar.dto;

import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.model.Turma;
import java.util.List;
import java.util.stream.Collectors;

public class AlunoDTO {
    private Long id;
    private String nome;
    private String email;
    private String curso;
    private List<TurmaDTO> turmas;

    public AlunoDTO(Aluno aluno) {
        this.id = aluno.getId();
        this.nome = aluno.getNome();
        this.email = aluno.getEmail();
        this.curso = aluno.getCurso();
        this.turmas = aluno.getTurmas().stream().map(TurmaDTO::new).collect(Collectors.toList());

    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getCurso() {
        return curso;
    }

    public List<TurmaDTO> getTurmas() {
        return turmas;
    }
}