package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.model.dto.aluno.AlunoResponse;
import br.com.ufpb.GerenciadorEscolar.model.dto.turma.TurmaRequest;
import br.com.ufpb.GerenciadorEscolar.model.dto.turma.TurmaResponse;
import br.com.ufpb.GerenciadorEscolar.mapper.TurmaMapper;
import br.com.ufpb.GerenciadorEscolar.mapper.AlunoMapper;
import br.com.ufpb.GerenciadorEscolar.model.entity.Turma;
import br.com.ufpb.GerenciadorEscolar.model.entity.Professor;
import br.com.ufpb.GerenciadorEscolar.model.entity.Aluno;
import br.com.ufpb.GerenciadorEscolar.repository.TurmaRepository;
import br.com.ufpb.GerenciadorEscolar.repository.ProfessorRepository;
import br.com.ufpb.GerenciadorEscolar.repository.AlunoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    /**
     * Criar uma nova turma.
     *
     * Este método cria uma nova turma e, opcionalmente, associa um professor a ela.
     *
     * @param turmaRequest - Objeto contendo os dados da nova turma.
     * @return TurmaResponse - Retorna a turma criada no formato de resposta.
     */
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
                            return new ProfessorNaoEncontradoException("Professor não encontrado");
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

    /**
     * Atualizar as informações de uma turma existente.
     *
     * Este método permite a atualização dos dados de uma turma, incluindo o professor responsável.
     *
     * @param id - ID da turma a ser atualizada.
     * @param turmaRequest - Objeto contendo os novos dados da turma.
     * @return TurmaResponse - Retorna os dados da turma atualizada.
     */
    @Override
    public TurmaResponse atualizarTurma(Long id, TurmaRequest turmaRequest) {
        log.info("Atualizando turma com ID: {}", id);
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Turma não encontrada para o ID: {}", id);
                    return new TurmaNaoEncontradaException("Turma não encontrada");
                });

        turma.setNome(turmaRequest.nome());
        turma.setSemestre(turmaRequest.semestre());
        turma.setCodigo(turmaRequest.codigo());

        if (turmaRequest.professorId() != null) {
            log.debug("Buscando professor para atualizar a turma com ID: {}", turmaRequest.professorId());
            Professor professor = professorRepository.findById(turmaRequest.professorId())
                    .orElseThrow(() -> {
                        log.error("Professor não encontrado para o ID: {}", turmaRequest.professorId());
                        return new ProfessorNaoEncontradoException("Professor não encontrado");
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

    /**
     * Buscar uma turma pelo seu ID.
     *
     * Este método recupera uma turma com base no ID informado.
     *
     * @param id - ID da turma a ser buscada.
     * @return Optional<TurmaResponse> - Retorna um `Optional` contendo a turma encontrada, se existir.
     */
    @Override
    public Optional<TurmaResponse> buscarTurmaPorId(Long id) {
        log.info("Buscando turma por ID: {}", id);
        Optional<Turma> turma = turmaRepository.findById(id);

        if (turma.isEmpty()) {
            log.warn("Turma não encontrada para o ID: {}", id);
            return Optional.empty();
        }

        return Optional.of(turmaMapper.toResponse(turma.get()));
    }

    /**
     * Deletar uma turma pelo seu ID.
     *
     * Este método remove uma turma do banco de dados, caso ela não possua alunos matriculados.
     *
     * @param id - ID da turma a ser deletada.
     * @throws RuntimeException - Caso existam alunos matriculados, a turma não pode ser deletada.
     */
    @Override
    public void deletarTurma(Long id) {
        log.info("Deletando turma com ID: {}", id);

        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Turma não encontrada para deleção com ID: {}", id);
                    return new TurmaNaoEncontradaException("Turma não encontrada");
                });

        if (!turma.getAlunos().isEmpty()) {
            log.warn("Tentativa de deletar turma com alunos matriculados. ID: {}", id);
            throw new RuntimeException("A turma ainda tem alunos matriculados e não pode ser deletada.");
        }

        turmaRepository.deleteById(id);
        log.info("Turma deletada com sucesso. ID: {}", id);
    }


    /**
     * Matricular um aluno em uma turma.
     *
     * Este método busca a turma e o aluno pelos respectivos IDs e realiza a matrícula,
     * garantindo que a turma não esteja lotada e que o aluno ainda não esteja matriculado.
     *
     * @param turmaId - ID da turma onde o aluno será matriculado.
     * @param alunoId - ID do aluno que será matriculado na turma.
     * @return TurmaResponse - Retorna a turma atualizada com o aluno matriculado.
     * @throws TurmaNaoEncontradaException - Se a turma não for encontrada.
     * @throws AlunoNaoEncontradoException - Se o aluno não for encontrado.
     * @throws TurmaLotadaException - Se a turma já atingiu o limite máximo de alunos.
     */
    @Override
    public TurmaResponse matricularAluno(Long turmaId, Long alunoId) {
        log.info("Matriculando aluno com ID: {} na turma com ID: {}", alunoId, turmaId);


        Turma turma = turmaRepository.findById(turmaId)
                .orElseThrow(() -> {
                    log.error("Turma não encontrada para matrícula com ID: {}", turmaId);
                    return new TurmaNaoEncontradaException("Turma não encontrada");
                });

        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> {
                    log.error("Aluno não encontrado para matrícula com ID: {}", alunoId);
                    return new AlunoNaoEncontradoException("Aluno não encontrado");
                });

        if (turma.getAlunos().size() >= turma.getTamanhoMaximo()) {
            log.warn("A turma com ID: {} já atingiu o tamanho máximo de alunos", turmaId);
            throw new TurmaLotadaException("A turma já atingiu o tamanho máximo de alunos.");
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

    /**
     * Listar alunos de uma turma com paginação.
     *
     * Este método busca a lista de alunos matriculados em uma turma específica
     * e retorna os resultados paginados.
     *
     * @param turmaId - ID da turma cujos alunos serão listados.
     * @param pageable - Objeto `Pageable` contendo informações de paginação.
     * @return Page<AlunoResponse> - Retorna uma página contendo os alunos da turma.
     * @throws TurmaNaoEncontradaException - Se a turma não for encontrada.
     */
    @Override
    public Page<AlunoResponse> listarAlunosPorTurma(Long turmaId, Pageable pageable) {
        log.info("Listando alunos da turma com ID: {} com paginação: {}", turmaId, pageable);

        if (!turmaRepository.existsById(turmaId)) {
            throw new TurmaNaoEncontradaException("Turma não encontrada");
        }

        return alunoRepository.findByTurmasId(turmaId, pageable)
                .map(alunoMapper::toResponse);
    }

    /**
     * Remover um aluno de uma turma.
     *
     * Este método busca a turma e o aluno pelos respectivos IDs e realiza a remoção do aluno,
     * garantindo que ele esteja matriculado antes de removê-lo.
     *
     * @param turmaId - ID da turma da qual o aluno será removido.
     * @param alunoId - ID do aluno que será removido da turma.
     * @throws TurmaNaoEncontradaException - Se a turma não for encontrada.
     * @throws AlunoNaoEncontradoException - Se o aluno não for encontrado.
     * @throws AlunoNaoMatriculadoException - Se o aluno não estiver matriculado na turma.
     */
    @Override
    public void removerAlunoDaTurma(Long turmaId, Long alunoId) {
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

    /**
     * Listar turmas em que um aluno está matriculado.
     *
     * Este método busca e retorna todas as turmas em que um aluno específico está matriculado,
     * garantindo que apenas turmas ativas sejam listadas.
     *
     * @param alunoId - ID do aluno cujas turmas serão listadas.
     * @param pageable - Objeto `Pageable` contendo informações de paginação.
     * @return Page<TurmaResponse> - Retorna uma página contendo as turmas do aluno.
     */
    @Override
    public Page<TurmaResponse> listarTurmasPorAluno(Long alunoId, Pageable pageable) {
        log.info("Listando turmas para aluno ID: {} com paginação: {}", alunoId, pageable);
        Page<Turma> turmas = turmaRepository.findByAlunosIdAndAtivoTrue(alunoId, pageable);
        log.info("Total de turmas encontradas para aluno {}: {}", alunoId, turmas.getContent().size());
        return turmas.map(turmaMapper::toResponse);
    }

    /**
     * Listar turmas associadas a um professor.
     *
     * Este método busca todas as turmas ativas vinculadas a um professor específico.
     *
     * @param professorId - ID do professor cujas turmas serão listadas.
     * @param pageable - Objeto `Pageable` contendo informações de paginação.
     * @return Page<TurmaResponse> - Retorna uma página contendo as turmas do professor.
     */
    @Override
    public Page<TurmaResponse> listarTurmasPorProfessor(Long professorId, Pageable pageable) {
        log.info("Listando turmas para professor ID: {} com paginação: {}", professorId, pageable);
        Page<Turma> turmas = turmaRepository.findByProfessorIdAndAtivoTrue(professorId, pageable);
        log.info("Total de turmas encontradas para professor {}: {}", professorId, turmas.getContent().size());
        return turmas.map(turmaMapper::toResponse);
    }

    /**
     * Listar todas as turmas cadastradas no sistema.
     *
     * Este método retorna uma lista paginada contendo todas as turmas, independentemente de estarem ativas ou inativas.
     *
     * @param pageable - Objeto `Pageable` contendo informações de paginação.
     * @return Page<TurmaResponse> - Retorna uma página contendo todas as turmas do sistema.
     */
    @Override
    public Page<TurmaResponse> listarTodasTurmas(Pageable pageable) {
        log.info("Listando todas as turmas com paginação: {}", pageable);
        Page<Turma> turmas = turmaRepository.findAll(pageable);
        log.info("Total de turmas encontradas: {}", turmas.getTotalElements());
        return turmas.map(turmaMapper::toResponse);
    }
}
