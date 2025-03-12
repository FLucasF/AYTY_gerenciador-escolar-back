package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.model.dto.aluno.AlunoResponse;
import br.com.ufpb.GerenciadorEscolar.model.dto.turma.TurmaRequest;
import br.com.ufpb.GerenciadorEscolar.model.dto.turma.TurmaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TurmaServiceInterface {

    TurmaResponse criarTurma(TurmaRequest turmaRequest);

    TurmaResponse atualizarTurma(Long id, TurmaRequest turmaRequest);

    Page<TurmaResponse> listarTodasTurmas(Pageable pageable);

    Optional<TurmaResponse> buscarTurmaPorId(Long id);

    void deletarTurma(Long id);


    TurmaResponse matricularAluno(Long turmaId, Long alunoId);

    Page<AlunoResponse> listarAlunosPorTurma(Long turmaId, Pageable pageable);

    void removerAlunoDaTurma(Long turmaId, Long alunoId);

    Page<TurmaResponse> listarTurmasPorAluno(Long alunoId, Pageable pageable);

    Page<TurmaResponse> listarTurmasPorProfessor(Long professorId, Pageable pageable);
}
