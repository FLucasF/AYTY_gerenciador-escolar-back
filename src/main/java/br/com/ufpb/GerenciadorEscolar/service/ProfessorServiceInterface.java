package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorRequest;
import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorResponse;
import br.com.ufpb.GerenciadorEscolar.model.Professor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProfessorServiceInterface {

    Page<ProfessorResponse> listarProfessoresAtivos(Pageable pageable);

    Optional<ProfessorResponse> buscarProfessorPorId(Long id);

    ProfessorResponse cadastrarProfessor(ProfessorRequest professorRequest);

    ProfessorResponse atualizarProfessor(Long id, ProfessorRequest professorRequest);

    void desativarProfessor(Long id);

    Optional<Professor> findByEmail(String email);
}
