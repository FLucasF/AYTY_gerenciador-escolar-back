package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoRequest;
import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoResponse;
import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AlunoServiceInterface {

    Page<AlunoResponse> listarAlunosAtivos(Pageable pageable);

    Optional<AlunoResponse> buscarAlunoPorId(Long id);

    AlunoResponse cadastrarAluno(AlunoRequest alunoRequest);

    AlunoResponse atualizarAluno(Long id, AlunoRequest alunoRequest);

    void desativarAluno(Long id);

    Optional<Aluno> findByEmail(String email);

}
