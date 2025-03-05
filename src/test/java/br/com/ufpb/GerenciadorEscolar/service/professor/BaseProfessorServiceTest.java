package br.com.ufpb.GerenciadorEscolar.service.professor;

import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorRequest;
import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorResponse;
import br.com.ufpb.GerenciadorEscolar.model.Professor;
import br.com.ufpb.GerenciadorEscolar.model.UserLogin;
import br.com.ufpb.GerenciadorEscolar.repository.ProfessorRepository;
import br.com.ufpb.GerenciadorEscolar.repository.UserLoginRepository;
import br.com.ufpb.GerenciadorEscolar.mapper.ProfessorMapper;
import br.com.ufpb.GerenciadorEscolar.service.ProfessorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

public abstract class BaseProfessorServiceTest {

    @Mock
    protected ProfessorRepository professorRepository;

    @Mock
    protected UserLoginRepository userLoginRepository;

    @Mock
    protected PasswordEncoder passwordEncoder;

    @Mock
    protected ProfessorMapper professorMapper;

    protected ProfessorServiceImpl professorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        professorService = new ProfessorServiceImpl(
                professorRepository,
                passwordEncoder,
                professorMapper,
                userLoginRepository
        );
    }

    protected Professor criarProfessorPadrao() {
        Professor professor = new Professor();
        professor.setId(1L);
        professor.setNome("Lucas Felipe");
        professor.setEmail("lucas@email.com");
        professor.setCpf("98765432100");
        professor.setDepartamento("Computação");
        professor.setSiape("1234567");
        professor.setSenha("senhaAntiga");
        return professor;
    }

    protected ProfessorResponse criarProfessorResponse(Professor professor) {
        return new ProfessorResponse(
                professor.getId(), professor.getNome(), professor.getEmail(), professor.getCpf(), professor.getDepartamento(), professor.getSiape()
        );
    }

    protected ProfessorRequest criarProfessorRequest(String nome, String email, String senha, String cpf, String departamento, String siape) {
        return new ProfessorRequest(nome, email, senha, cpf, departamento, siape);
    }

    protected UserLogin criarUserLoginPadrao(Professor professor) {
        UserLogin userLogin = new UserLogin();
        userLogin.setUsuario(professor);
        userLogin.setEmail(professor.getEmail());
        userLogin.setSenha(professor.getSenha());
        return userLogin;
    }

    protected Professor criarProfessorAtivo() {
        Professor professor = new Professor();
        professor.setId(1L);
        professor.setAtivo(true);
        return professor;
    }

    protected Professor criarProfessorInativo() {
        Professor professor = new Professor();
        professor.setId(1L);
        professor.setAtivo(false);
        return professor;
    }

    protected UserLogin criarUserLoginAtivo() {
        UserLogin userLogin = new UserLogin();
        userLogin.setAtivo(true);
        return userLogin;
    }

    protected UserLogin criarUserLoginInativo() {
        UserLogin userLogin = new UserLogin();
        userLogin.setAtivo(false);
        return userLogin;
    }
}
