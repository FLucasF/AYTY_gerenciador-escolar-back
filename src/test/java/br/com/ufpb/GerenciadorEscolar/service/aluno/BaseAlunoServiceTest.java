package br.com.ufpb.GerenciadorEscolar.service.aluno;

import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoRequest;
import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoResponse;
import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.model.UserLogin;
import br.com.ufpb.GerenciadorEscolar.repository.AlunoRepository;
import br.com.ufpb.GerenciadorEscolar.repository.UserLoginRepository;
import br.com.ufpb.GerenciadorEscolar.mapper.AlunoMapper;
import br.com.ufpb.GerenciadorEscolar.service.AlunoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

public abstract class BaseAlunoServiceTest {

    @Mock
    protected AlunoRepository alunoRepository;

    @Mock
    protected UserLoginRepository userLoginRepository;

    @Mock
    protected PasswordEncoder passwordEncoder;

    @Mock
    protected AlunoMapper alunoMapper;

    protected AlunoServiceImpl alunoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        alunoService = new AlunoServiceImpl(
                alunoRepository,
                passwordEncoder,
                alunoMapper,
                userLoginRepository
        );
    }

    /**
     * Cria um objeto Aluno padrão para ser utilizado nos testes.
     */
    protected Aluno criarAlunoPadrao() {
        Aluno aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("Lucas Felipe");
        aluno.setEmail("lucas@email.com");
        aluno.setCpf("12345678901");
        aluno.setCurso("Engenharia");
        aluno.setSenha("Senha@123"); // Senha válida conforme os requisitos
        aluno.setAtivo(true);
        return aluno;
    }

    /**
     * Cria um AlunoResponse baseado em um Aluno.
     */
    protected AlunoResponse criarAlunoResponse(Aluno aluno) {
        return new AlunoResponse(
                aluno.getId(), aluno.getNome(), aluno.getEmail(), aluno.getCpf(), aluno.getCurso()
        );
    }

    /**
     * Cria um AlunoRequest customizável.
     */
    protected AlunoRequest criarAlunoRequest(String nome, String email, String senha, String cpf, String curso) {
        return new AlunoRequest(nome, email, senha, cpf, curso);
    }

    /**
     * Cria um AlunoRequest padrão para ser utilizado nos testes.
     */
    protected AlunoRequest criarAlunoRequestPadrao() {
        return new AlunoRequest("Lucas Felipe", "lucas@email.com", "Senha@123", "12345678901", "Engenharia");
    }

    /**
     * Cria um UserLogin padrão para ser associado a um Aluno.
     */
    protected UserLogin criarUserLoginPadrao(Aluno aluno) {
        UserLogin userLogin = new UserLogin();
        userLogin.setUsuario(aluno);
        userLogin.setEmail(aluno.getEmail());
        userLogin.setSenha(aluno.getSenha());
        userLogin.setAtivo(true);
        return userLogin;
    }

    /**
     * Cria um Aluno ativo.
     */
    protected Aluno criarAlunoAtivo() {
        Aluno aluno = criarAlunoPadrao();
        aluno.setAtivo(true);
        return aluno;
    }

    /**
     * Cria um Aluno inativo.
     */
    protected Aluno criarAlunoInativo() {
        Aluno aluno = criarAlunoPadrao();
        aluno.setAtivo(false);
        return aluno;
    }

    /**
     * Cria um UserLogin ativo.
     */
    protected UserLogin criarUserLoginAtivo() {
        UserLogin userLogin = new UserLogin();
        userLogin.setAtivo(true);
        return userLogin;
    }

    /**
     * Cria um UserLogin inativo.
     */
    protected UserLogin criarUserLoginInativo() {
        UserLogin userLogin = new UserLogin();
        userLogin.setAtivo(false);
        return userLogin;
    }
}
