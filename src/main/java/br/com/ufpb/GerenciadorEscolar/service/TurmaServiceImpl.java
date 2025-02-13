package br.com.ufpb.GerenciadorEscolar.service.impl;

import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.model.Turma;
import br.com.ufpb.GerenciadorEscolar.repository.AlunoRepository;
import br.com.ufpb.GerenciadorEscolar.repository.TurmaRepository;
import br.com.ufpb.GerenciadorEscolar.service.TurmaServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class TurmaServiceImpl implements TurmaServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(TurmaServiceImpl.class);

    @Autowired
    private TurmaRepository turmaRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Override
    public Turma matricularAluno(Long turmaId, Long alunoId) {
        logger.info("Tentando matricular o aluno {} na turma {}", alunoId, turmaId);

        Optional<Turma> turmaOpt = turmaRepository.findById(turmaId);
        Optional<Aluno> alunoOpt = alunoRepository.findById(alunoId);

        if (turmaOpt.isEmpty()) {
            logger.error("Turma com ID {} não encontrada!", turmaId);
            throw new RuntimeException("Turma não encontrada.");
        }
        if (alunoOpt.isEmpty()) {
            logger.error("Aluno com ID {} não encontrado!", alunoId);
            throw new RuntimeException("Aluno não encontrado.");
        }

        Turma turma = turmaOpt.get();
        Aluno aluno = alunoOpt.get();

        logger.info("Aluno {} encontrado: {}", aluno.getId(), aluno.getNome());
        logger.info("Turma {} encontrada: {}", turma.getId(), turma.getNome());

        turma.getAlunos().add(aluno);
        aluno.getTurmas().add(turma);

        alunoRepository.save(aluno);
        return turmaRepository.save(turma);
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
