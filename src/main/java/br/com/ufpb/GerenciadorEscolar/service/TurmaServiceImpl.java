package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoResponse;
import br.com.ufpb.GerenciadorEscolar.dto.turma.TurmaRequest;
import br.com.ufpb.GerenciadorEscolar.dto.turma.TurmaResponse;
import br.com.ufpb.GerenciadorEscolar.mapper.TurmaMapper;
import br.com.ufpb.GerenciadorEscolar.mapper.AlunoMapper;
import br.com.ufpb.GerenciadorEscolar.model.Turma;
import br.com.ufpb.GerenciadorEscolar.model.Professor;
import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.repository.TurmaRepository;
import br.com.ufpb.GerenciadorEscolar.repository.ProfessorRepository;
import br.com.ufpb.GerenciadorEscolar.repository.AlunoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class TurmaServiceImpl implements TurmaServiceInterface {

    private final TurmaRepository turmaRepository;
    private final ProfessorRepository professorRepository;
    private final AlunoRepository alunoRepository;
    private final TurmaMapper turmaMapper;
    private final AlunoMapper alunoMapper;

    @Autowired
    public TurmaServiceImpl(TurmaRepository turmaRepository,
                            ProfessorRepository professorRepository,
                            AlunoRepository alunoRepository,
                            TurmaMapper turmaMapper,
                            AlunoMapper alunoMapper) {
        this.turmaRepository = turmaRepository;
        this.professorRepository = professorRepository;
        this.alunoRepository = alunoRepository;
        this.turmaMapper = turmaMapper;
        this.alunoMapper = alunoMapper;
    }

    @Override
    public TurmaResponse criarTurma(TurmaRequest turmaRequest) {
        log.info("Recebendo requisição para criar turma: {}", turmaRequest);

        List<Map.Entry<String, String>> campos = Arrays.asList(
                new AbstractMap.SimpleEntry<>("Nome", turmaRequest.nome()),
                new AbstractMap.SimpleEntry<>("Código", turmaRequest.codigo()),
                new AbstractMap.SimpleEntry<>("Semestre", turmaRequest.semestre())
        );

        campos.forEach(campo -> {
            if (campo.getValue() == null) {
                throw new NullPointerException("O campo " + campo.getKey() + " não pode ser nulo.");
            }
            if (campo.getValue().trim().isEmpty()) {
                throw new IllegalArgumentException("O campo " + campo.getKey() + " não pode ser vazio.");
            }
        });

        Turma turma = turmaMapper.toEntity(turmaRequest);

        if (turmaRequest.professorId() != null) {
            log.debug("Buscando professor com ID: {}", turmaRequest.professorId());
            Professor professor = professorRepository.findById(turmaRequest.professorId())
                    .orElseThrow(() -> {
                        log.error("Professor não encontrado para o ID: {}", turmaRequest.professorId());
                        return new RuntimeException("Professor não encontrado");
                    });
            turma.setProfessor(professor);
            log.info("Professor associado à turma: {}", professor.getNome());
        }

        turma = turmaRepository.save(turma);
        log.debug("Turma salva com ID: {}", turma.getId());

        TurmaResponse response = turmaMapper.toResponse(turma);
        log.info("Retornando resposta da turma: {}", response);

        return response;
    }

    @Override
    public TurmaResponse atualizarTurma(Long id, TurmaRequest turmaRequest) {
        log.info("Atualizando turma com ID: {}", id);
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Turma não encontrada para o ID: {}", id);
                    return new RuntimeException("Turma não encontrada");
                });

        turma.setNome(turmaRequest.nome());
        turma.setSemestre(turmaRequest.semestre());
        turma.setCodigo(turmaRequest.codigo());

        if (turmaRequest.professorId() != null) {
            log.debug("Buscando professor para atualizar a turma com ID: {}", turmaRequest.professorId());
            Professor professor = professorRepository.findById(turmaRequest.professorId())
                    .orElseThrow(() -> {
                        log.error("Professor não encontrado para o ID: {}", turmaRequest.professorId());
                        return new RuntimeException("Professor não encontrado");
                    });
            turma.setProfessor(professor);
            log.info("Professor associado atualizado: {}", professor.getId());
        } else {
            turma.setProfessor(null);
            log.warn("Professor removido da turma com ID: {}", id);
        }

        turma = turmaRepository.saveAndFlush(turma);
        log.debug("Após salvar, professor associado na turma: {}",
                turma.getProfessor() != null ? turma.getProfessor().getId() : "null");

        Long professorId = (turma.getProfessor() != null) ? turma.getProfessor().getId() : null;
        TurmaResponse response = new TurmaResponse(
                turma.getId(),
                turma.getNome(),
                turma.getCodigo(),
                turma.getSemestre(),
                professorId
        );
        log.info("Turma atualizada com sucesso: {}", response);
        return response;
    }

    @Override
    public Optional<TurmaResponse> buscarTurmaPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID da turma não pode ser nulo");
        }
        if (id < 0) {
            throw new IllegalArgumentException("ID da turma não pode ser negativo");
        }

        log.info("Buscando turma por ID: {}", id);
        Optional<Turma> turma = turmaRepository.findById(id);

        if (turma.isEmpty()) {
            log.warn("Turma não encontrada para o ID: {}", id);
            return Optional.empty();
        }

        return Optional.of(turmaMapper.toResponse(turma.get()));
    }





    @Override
    public void deletarTurma(Long id) {
        log.info("Deletando turma com ID: {}", id);

        if (id == null) {
            throw new IllegalArgumentException("ID da turma não pode ser nulo");
        }

        if (id <= 0) {
            throw new IllegalArgumentException("ID da turma não pode ser negativo");
        }

        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Turma não encontrada para deleção com ID: {}", id);
                    return new RuntimeException("Turma não encontrada");
                });

        if (!turma.getAlunos().isEmpty()) {
            log.warn("Tentativa de deletar turma com alunos matriculados. ID: {}", id);
            throw new RuntimeException("A turma ainda tem alunos matriculados e não pode ser deletada.");
        }

        turmaRepository.deleteById(id);
        log.info("Turma deletada com sucesso. ID: {}", id);
    }


    @Override
    public TurmaResponse matricularAluno(Long turmaId, Long alunoId) {
        log.info("Matriculando aluno com ID: {} na turma com ID: {}", alunoId, turmaId);

        validarIds(turmaId, alunoId);


        Turma turma = turmaRepository.findById(turmaId)
                .orElseThrow(() -> {
                    log.error("Turma não encontrada para matrícula com ID: {}", turmaId);
                    return new RuntimeException("Turma não encontrada");
                });

        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> {
                    log.error("Aluno não encontrado para matrícula com ID: {}", alunoId);
                    return new RuntimeException("Aluno não encontrado");
                });

        //VERIFICAR SE É ASSIM MESMO
        if (turma.getAlunos() == null) {
            turma.setAlunos(new ArrayList<>());
        }
        if (aluno.getTurmas() == null) {
            aluno.setTurmas(new ArrayList<>());
        }

        if (turma.getAlunos().size() >= turma.getTamanhoMaximo()) {
            log.warn("A turma com ID: {} já atingiu o tamanho máximo de alunos", turmaId);
            throw new RuntimeException("A turma já atingiu o tamanho máximo de alunos.");
        }

        if (!turma.getAlunos().contains(aluno)) {
            turma.getAlunos().add(aluno);
            aluno.getTurmas().add(turma);
            turmaRepository.save(turma);
            log.info("Aluno matriculado com sucesso na turma.");
        } else {
            log.debug("Aluno já estava matriculado na turma. Aluno ID: {}", alunoId);
        }

        return turmaMapper.toResponse(turma);
    }

    @Override
    public Page<AlunoResponse> listarAlunosPorTurma(Long turmaId, Pageable pageable) {
        log.info("Listando alunos da turma com ID: {} com paginação: {}", turmaId, pageable);

        if (turmaId == null) {
            throw new IllegalArgumentException("ID da turma não pode ser nulo");
        }
        if (turmaId < 0) {
            throw new IllegalArgumentException("ID da turma não pode ser negativo");
        }

        if (!turmaRepository.existsById(turmaId)) {
            throw new RuntimeException("Turma não encontrada");
        }

        return alunoRepository.findByTurmasId(turmaId, pageable)
                .map(alunoMapper::toResponse);
    }

    @Override
    public void removerAlunoDaTurma(Long turmaId, Long alunoId) {
        validarIds(turmaId, alunoId);

        log.info("Removendo aluno com ID: {} da turma com ID: {}", alunoId, turmaId);

        // Busca turma e aluno diretamente, lançando exceção caso não existam
        Turma turma = turmaRepository.findById(turmaId)
                .orElseThrow(() -> new TurmaNaoEncontradaException("Turma não encontrada"));

        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new AlunoNaoEncontradoException("Aluno não encontrado"));

        if (!turma.getAlunos().contains(aluno)) {
            log.warn("Aluno ID {} não está matriculado na turma ID {}", alunoId, turmaId);
            throw new AlunoNaoMatriculadoException("Aluno não está matriculado nesta turma.");
        }

        // Remove aluno da turma e atualiza no banco
        turma.getAlunos().remove(aluno);
        aluno.getTurmas().remove(turma);
        turmaRepository.save(turma);

        log.info("Aluno ID {} removido com sucesso da turma ID {}", alunoId, turmaId);
    }

    @Override
    public Page<TurmaResponse> listarTurmasPorAluno(Long alunoId, Pageable pageable) {
        if (alunoId == null) {
            throw new NullPointerException("ID do aluno não pode ser nulo");
        }
        if (alunoId < 0) {
            throw new IllegalArgumentException("ID do aluno não pode ser negativo");
        }
        log.info("Listando turmas para aluno ID: {} com paginação: {}", alunoId, pageable);
        Page<Turma> turmas = turmaRepository.findByAlunosIdAndAtivoTrue(alunoId, pageable);
        log.info("Total de turmas encontradas para aluno {}: {}", alunoId, turmas.getContent().size());
        return turmas.map(turmaMapper::toResponse);
    }

    @Override
    public Page<TurmaResponse> listarTurmasPorProfessor(Long professorId, Pageable pageable) {
        if (professorId == null) {
            throw new NullPointerException("ID do aluno não pode ser nulo");
        }
        if (professorId < 0) {
            throw new IllegalArgumentException("ID do aluno não pode ser negativo");
        }
        log.info("Listando turmas para professor ID: {} com paginação: {}", professorId, pageable);
        Page<Turma> turmas = turmaRepository.findByProfessorIdAndAtivoTrue(professorId, pageable);
        log.info("Total de turmas encontradas para professor {}: {}", professorId, turmas.getContent().size());
        return turmas.map(turmaMapper::toResponse);
    }

    @Override
    public Page<TurmaResponse> listarTodasTurmas(Pageable pageable) {
        log.info("Listando todas as turmas com paginação: {}", pageable);
        Page<Turma> turmas = turmaRepository.findAll(pageable);
        log.info("Total de turmas encontradas: {}", turmas.getTotalElements());
        return turmas.map(turmaMapper::toResponse);
    }


    private void validarIds(Long turmaId, Long alunoId) {
        if (turmaId == null || alunoId == null) {
            throw new NullPointerException("ID não pode ser nulo");
        }
        if (turmaId < 0 || alunoId < 0) {
            throw new IllegalArgumentException("ID não pode ser negativo");
        }
    }
}
