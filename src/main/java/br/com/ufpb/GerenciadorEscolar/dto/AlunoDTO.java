package br.com.ufpb.GerenciadorEscolar.dto;

import java.util.List;

public class AlunoDTO extends UsuarioDTO {
    private String curso;
    private List<String> turmas;

    public AlunoDTO(Long id, String nome, String email, String curso, List<String> turmas) {
        super(id, nome, email);
        this.curso = curso;
        this.turmas = turmas;
    }

    public String getCurso() {
        return curso;
    }

    public List<String> getTurmas() {
        return turmas;
    }
}
