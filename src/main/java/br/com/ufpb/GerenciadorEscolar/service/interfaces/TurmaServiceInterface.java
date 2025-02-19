package br.com.ufpb.GerenciadorEscolar.service.interfaces;

import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.model.Turma;
import java.util.List;
import java.util.Optional;

public interface TurmaServiceInterface {
    Turma criarTurma(Turma turma);
    Turma atualizarTurma(Long id, Turma novaTurma);
    List<Turma> listarTodasTurmas();
    Optional<Turma> buscarTurmaPorId(Long id);
    void deletarTurma(Long id);
    Turma matricularAluno(Long turmaId, Long alunoId);
    Turma removerAlunoDaTurma(Long turmaId, Long alunoId);
    List<Aluno> listarAlunosPorTurma(Long turmaId);
    List<Turma> listarTurmasPorProfessor(Long professorId);
}
