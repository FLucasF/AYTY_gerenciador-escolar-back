package br.com.ufpb.GerenciadorEscolar.service;
import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.model.Turma;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TurmaServiceInterface {

    Turma criarTurma(Turma turma);

    Turma atualizarTurma(Long id, Turma novaTurma);

    // Alterado para retornar Page
    Page<Turma> listarTodasTurmas(Pageable pageable);

    Optional<Turma> buscarTurmaPorId(Long id);

    void deletarTurma(Long id);

    Turma matricularAluno(Long turmaId, Long alunoId);

    Turma removerAlunoDaTurma(Long turmaId, Long alunoId);

    // Já retorna Page para alunos
    Page<Aluno> listarAlunosPorTurma(Long turmaId, Pageable pageable);

    // Novo: listagem de turmas por professor com paginação
    Page<Turma> listarTurmasPorProfessor(Long professorId, Pageable pageable);
}
