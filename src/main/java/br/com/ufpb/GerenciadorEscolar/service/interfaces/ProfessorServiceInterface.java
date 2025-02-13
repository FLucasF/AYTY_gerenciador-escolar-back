package br.com.ufpb.GerenciadorEscolar.service.interfaces;

import br.com.ufpb.GerenciadorEscolar.model.Professor;
import java.util.List;
import java.util.Optional;

public interface ProfessorServiceInterface {
    List<Professor> listarProfessoresAtivos();
    Optional<Professor> buscarProfessorPorId(Long id);
    Professor cadastrarProfessor(Professor professor);
    Professor atualizarProfessor(Long id, Professor professor);
    void desativarProfessor(Long id);
}
