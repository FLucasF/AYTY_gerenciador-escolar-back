package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.model.Turma;
import java.util.List;
import java.util.Optional;

public interface TurmaServiceInterface {
    Turma criarTurma(Turma turma);
    List<Turma> listarTodasTurmas();
    Optional<Turma> buscarTurmaPorId(Long id);
    void deletarTurma(Long id);
    Turma matricularAluno(Long turmaId, Long alunoId);
}
