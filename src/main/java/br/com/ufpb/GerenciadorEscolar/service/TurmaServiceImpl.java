package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.model.Professor;
import br.com.ufpb.GerenciadorEscolar.model.Turma;
import br.com.ufpb.GerenciadorEscolar.repository.AlunoRepository;
import br.com.ufpb.GerenciadorEscolar.repository.ProfessorRepository;
import br.com.ufpb.GerenciadorEscolar.repository.TurmaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Service
public class TurmaServiceImpl implements TurmaServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(TurmaServiceImpl.class);

    private final TurmaRepository turmaRepository;
    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;

    @Autowired
    public TurmaServiceImpl(TurmaRepository turmaRepository, AlunoRepository alunoRepository, ProfessorRepository professorRepository) {
        this.turmaRepository = turmaRepository;
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
    }

    @Override
    public Turma criarTurma(Turma turma) {
        if (turma.getProfessor() != null && turma.getProfessor().getId() != null) {
            Professor professor = professorRepository.findById(turma.getProfessor().getId())
                    .orElseThrow(() -> new RuntimeException("Professor não encontrado"));
            turma.setProfessor(professor);
        }
        return turmaRepository.save(turma);
    }

    @Override
    public Turma atualizarTurma(Long id, Turma novaTurma) {
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("⚠️ Turma não encontrada"));
        turma.setNome(novaTurma.getNome());
        turma.setCodigo(novaTurma.getCodigo());
        turma.setSemestre(novaTurma.getSemestre());
        if (novaTurma.getProfessor() != null && novaTurma.getProfessor().getId() != null) {
            Professor professor = professorRepository.findById(novaTurma.getProfessor().getId())
                    .orElseThrow(() -> new RuntimeException("⚠️ Professor não encontrado"));
            turma.setProfessor(professor);
        } else {
            turma.setProfessor(null);
        }
        Turma turmaAtualizada = turmaRepository.save(turma);
        logger.info("✏️ Turma '{}' (ID: {}) atualizada com sucesso!", turmaAtualizada.getNome(), turmaAtualizada.getId());
        return turmaAtualizada;
    }

    @Override
    public Page<Turma> listarTodasTurmas(Pageable pageable) {
        return turmaRepository.findAll(pageable);
    }

    @Override
    public Optional<Turma> buscarTurmaPorId(Long id) {
        return turmaRepository.findById(id);
    }

    @Override
    public void deletarTurma(Long id) {
        turmaRepository.deleteById(id);
    }

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
    public Turma removerAlunoDaTurma(Long turmaId, Long alunoId) {
        Turma turma = turmaRepository.findById(turmaId)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada"));
        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        turma.getAlunos().remove(aluno);
        aluno.getTurmas().remove(turma);

        alunoRepository.save(aluno);
        return turmaRepository.save(turma);
    }

    @Override
    public Page<Aluno> listarAlunosPorTurma(Long turmaId, Pageable pageable) {
        return alunoRepository.findByTurmasId(turmaId, pageable);
    }

    @Override
    public Page<Turma> listarTurmasPorProfessor(Long professorId, Pageable pageable) {
        return turmaRepository.findByProfessorId(professorId, pageable);
    }
}
