package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoResponse;
import br.com.ufpb.GerenciadorEscolar.dto.turma.TurmaRequest;
import br.com.ufpb.GerenciadorEscolar.dto.turma.TurmaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TurmaServiceInterface {

    /**
     * Cria uma nova turma com base nos dados fornecidos no DTO de request.
     */
    TurmaResponse criarTurma(TurmaRequest turmaRequest);

    /**
     * Atualiza os dados de uma turma existente com base no ID e nos dados fornecidos.
     */
    TurmaResponse atualizarTurma(Long id, TurmaRequest turmaRequest);

    /**
     * Lista todas as turmas disponíveis com paginação.
     */
    Page<TurmaResponse> listarTodasTurmas(Pageable pageable);

    /**
     * Busca uma turma pelo ID e retorna um Optional contendo a resposta se encontrada.
     */
    Optional<TurmaResponse> buscarTurmaPorId(Long id);

    /**
     * Deleta uma turma com base no ID.
     */
    void deletarTurma(Long id);

    /**
     * Matricula um aluno em uma turma específica.
     */
    TurmaResponse matricularAluno(Long turmaId, Long alunoId);

    /**
     * Lista todas as turmas associadas a um professor com paginação.
     */
    Page<TurmaResponse> listarTurmasPorProfessor(Long professorId, Pageable pageable);

    Page<AlunoResponse> listarAlunosPorTurma(Long turmaId, Pageable pageable);
    Page<AlunoResponse> removerAlunoDaTurma(Long turmaId, Long alunoId);
}
