package br.com.ufpb.GerenciadorEscolar.dto;

import java.util.List;

public class ProfessorDTO extends UsuarioDTO {
    private String departamento;
    private List<String> turmas;

    public ProfessorDTO(Long id, String nome, String email, String departamento, List<String> turmas) {
        super(id, nome, email);
        this.departamento = departamento;
        this.turmas = turmas;
    }

    public String getDepartamento() {
        return departamento;
    }

    public List<String> getTurmas() {
        return turmas;
    }
}
