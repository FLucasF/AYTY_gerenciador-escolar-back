package br.com.ufpb.GerenciadorEscolar.service.impl;

import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.model.Turma;
import br.com.ufpb.GerenciadorEscolar.repository.AlunoRepository;
import br.com.ufpb.GerenciadorEscolar.repository.TurmaRepository;
import br.com.ufpb.GerenciadorEscolar.service.TurmaServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TurmaServiceImpl implements TurmaServiceInterface {

    @Autowired
    private TurmaRepository turmaRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Override
    public Turma matricularAluno(Long turmaId, Long alunoId) {
        Optional<Turma> turmaOpt = turmaRepository.findById(turmaId);
        Optional<Aluno> alunoOpt = alunoRepository.findById(alunoId);

        if (turmaOpt.isPresent() && alunoOpt.isPresent()) {
            Turma turma = turmaOpt.get();
            Aluno aluno = alunoOpt.get();

            turma.getAlunos().add(aluno);
            aluno.getTurmas().add(turma);

            alunoRepository.save(aluno);
            return turmaRepository.save(turma);
        } else {
            throw new RuntimeException("Turma ou Aluno não encontrados.");
        }
    }

    @Override
    public Turma criarTurma(Turma turma) {
        return turmaRepository.save(turma);
    }

    @Override
    public List<Turma> listarTodasTurmas() {
        return turmaRepository.findAll();
    }

    @Override
    public Optional<Turma> buscarTurmaPorId(Long id) {
        return turmaRepository.findById(id);
    }

    @Override
    public void deletarTurma(Long id) {
        turmaRepository.deleteById(id);
    }
}
