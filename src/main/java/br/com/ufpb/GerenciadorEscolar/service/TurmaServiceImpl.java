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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TurmaServiceImpl implements TurmaServiceInterface {

    private final TurmaRepository turmaRepository;
    private final ProfessorRepository professorRepository;
    private final AlunoRepository alunoRepository;
    private final TurmaMapper turmaMapper;
    private final AlunoMapper alunoMapper; // 🔹 Injetado para conversão de Aluno -> AlunoResponse

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
        Turma turma = turmaMapper.toEntity(turmaRequest); // 🔹 Agora apenas mapeia os dados básicos

        // 🔹 Resolver professor
        Professor professor = professorRepository.findById(turmaRequest.professorId())
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));
        turma.setProfessor(professor);

        turmaRepository.save(turma);
        return turmaMapper.toResponse(turma);
    }

    @Override
    public TurmaResponse atualizarTurma(Long id, TurmaRequest turmaRequest) {
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada"));

        turma.setNome(turmaRequest.nome());
        turma.setCodigo(turmaRequest.codigo());
        turma.setSemestre(turmaRequest.semestre());

        // 🔹 Resolver professor
        Professor professor = professorRepository.findById(turmaRequest.professorId())
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));
        turma.setProfessor(professor);

        turmaRepository.save(turma);
        return turmaMapper.toResponse(turma);
    }

    @Override
    public Page<TurmaResponse> listarTodasTurmas(Pageable pageable) {
        return turmaRepository.findAll(pageable).map(turmaMapper::toResponse);
    }

    @Override
    public Optional<TurmaResponse> buscarTurmaPorId(Long id) {
        return turmaRepository.findById(id).map(turmaMapper::toResponse);
    }

    @Override
    public void deletarTurma(Long id) {
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada"));

        if (!turma.getAlunos().isEmpty()) {
            throw new RuntimeException("A turma ainda tem alunos matriculados e não pode ser deletada.");
        }

        turmaRepository.deleteById(id);
    }

    @Override
    public TurmaResponse matricularAluno(Long turmaId, Long alunoId) {
        Turma turma = turmaRepository.findById(turmaId)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada"));

        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        // 🔹 Verifica se o aluno já está matriculado
        if (!turma.getAlunos().contains(aluno)) {
            turma.getAlunos().add(aluno);
            aluno.getTurmas().add(turma);
            turmaRepository.save(turma);
        }

        return turmaMapper.toResponse(turma);
    }

    @Override
    public Page<TurmaResponse> listarTurmasPorProfessor(Long professorId, Pageable pageable) {
        return turmaRepository.findByProfessorId(professorId, pageable).map(turmaMapper::toResponse);
    }

    @Override
    public Page<AlunoResponse> listarAlunosPorTurma(Long turmaId, Pageable pageable) {
        Turma turma = turmaRepository.findById(turmaId)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada"));

        List<AlunoResponse> alunos = turma.getAlunos()
                .stream()
                .map(alunoMapper::toResponse) // 🔹 Usa o AlunoMapper para converter
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), alunos.size());

        return new PageImpl<>(alunos.subList(start, end), pageable, alunos.size());
    }

    @Override
    public Page<AlunoResponse> removerAlunoDaTurma(Long turmaId, Long alunoId) {
        Turma turma = turmaRepository.findById(turmaId)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada"));

        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        // 🔹 Remove o aluno da turma
        if (turma.getAlunos().contains(aluno)) {
            turma.getAlunos().remove(aluno);
            aluno.getTurmas().remove(turma);
            turmaRepository.save(turma);
        } else {
            throw new RuntimeException("Aluno não está matriculado nesta turma.");
        }

        // 🔹 Retorna a lista atualizada de alunos
        List<AlunoResponse> alunosAtualizados = turma.getAlunos()
                .stream()
                .map(alunoMapper::toResponse)
                .toList();

        return new PageImpl<>(alunosAtualizados);
    }

}
