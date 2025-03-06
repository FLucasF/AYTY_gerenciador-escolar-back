package br.com.ufpb.GerenciadorEscolar.service.turma;

import br.com.ufpb.GerenciadorEscolar.dto.turma.TurmaRequest;
import br.com.ufpb.GerenciadorEscolar.dto.turma.TurmaResponse;
import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoResponse;
import br.com.ufpb.GerenciadorEscolar.mapper.AlunoMapper;
import br.com.ufpb.GerenciadorEscolar.mapper.TurmaMapper;
import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.model.Professor;
import br.com.ufpb.GerenciadorEscolar.model.Turma;
import br.com.ufpb.GerenciadorEscolar.repository.AlunoRepository;
import br.com.ufpb.GerenciadorEscolar.repository.ProfessorRepository;
import br.com.ufpb.GerenciadorEscolar.repository.TurmaRepository;
import br.com.ufpb.GerenciadorEscolar.service.TurmaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public abstract class BaseTurmaServiceTest {

    @Mock
    protected TurmaRepository turmaRepository;

    @Mock
    protected ProfessorRepository professorRepository;

    @Mock
    protected AlunoRepository alunoRepository;

    @Mock
    protected TurmaMapper turmaMapper;

    @Mock
    protected AlunoMapper alunoMapper;

    @InjectMocks
    protected TurmaServiceImpl turmaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        System.out.println("turmaRepository: " + turmaRepository);
        System.out.println("professorRepository: " + professorRepository);
        System.out.println("alunoRepository: " + alunoRepository);
        System.out.println("turmaMapper: " + turmaMapper);
        System.out.println("alunoMapper: " + alunoMapper);
    }

    protected Turma criarTurmaPadrao() {
        Turma turma = new Turma();
        turma.setId(1L);
        turma.setNome("Turma A");
        turma.setCodigo("TURMA123");
        turma.setSemestre("2024.1");
        return turma;
    }

    protected Professor criarProfessorPadrao() {
        Professor professor = new Professor();
        professor.setId(1L);
        professor.setNome("Professor Teste");
        return professor;
    }

    protected Aluno criarAlunoPadrao() {
        Aluno aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("Aluno Teste");
        return aluno;
    }

    protected TurmaResponse criarTurmaResponse(Turma turma) {
        return new TurmaResponse(
                turma.getId(), turma.getNome(), turma.getCodigo(), turma.getSemestre(),
                turma.getProfessor() != null ? turma.getProfessor().getId() : null
        );
    }

    protected TurmaRequest criarTurmaRequest(String nome, String codigo, String semestre, Long professorId) {
        return new TurmaRequest(nome, codigo, semestre, professorId);
    }
}
