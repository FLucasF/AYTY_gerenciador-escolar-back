//package br.com.ufpb.GerenciadorEscolar.service.professor;
//
//import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorRequest;
//import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorResponse;
//import br.com.ufpb.GerenciadorEscolar.model.entity.Professor;
//import br.com.ufpb.GerenciadorEscolar.model.entity.UserLogin;
//import br.com.ufpb.GerenciadorEscolar.repository.ProfessorRepository;
//import br.com.ufpb.GerenciadorEscolar.repository.UserLoginRepository;
//import br.com.ufpb.GerenciadorEscolar.mapper.ProfessorMapper;
//import br.com.ufpb.GerenciadorEscolar.service.ProfessorServiceImpl;
//import br.com.ufpb.GerenciadorEscolar.util.UsuarioUtils;
//import org.junit.jupiter.api.BeforeEach;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//public abstract class BaseProfessorServiceTest {
//
//    @Mock
//    protected ProfessorRepository professorRepository;
//
//    @Mock
//    protected UsuarioUtils usuarioUtils;
//
//    @Mock
//    protected UserLoginRepository userLoginRepository;
//
//    @Mock
//    protected PasswordEncoder passwordEncoder;
//
//    @Mock
//    protected ProfessorMapper professorMapper;
//
//    protected ProfessorServiceImpl professorService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        professorService = new ProfessorServiceImpl(
//                professorRepository,
//                passwordEncoder,
//                professorMapper,
//                userLoginRepository,
//                usuarioUtils
//        );
//    }
//
//    /**
//     * Cria um Professor padrão para os testes.
//     */
//    protected Professor criarProfessorPadrao() {
//        Professor professor = new Professor();
//        professor.setId(1L);
//        professor.setNome("Lucas Felipe");
//        professor.setEmail("lucas@email.com");
//        professor.setCpf("98765432100");
//        professor.setDepartamento("Computação");
//        professor.setSiape("1234567");
//        professor.setSenha("Senha@123"); // Senha segura conforme requisitos
//        professor.setAtivo(true);
//        return professor;
//    }
//
//    /**
//     * Cria um ProfessorResponse com base em um Professor.
//     */
//    protected ProfessorResponse criarProfessorResponse(Professor professor) {
//        return new ProfessorResponse(
//                professor.getId(),
//                professor.getNome(),
//                professor.getEmail(),
//                professor.getCpf(),
//                professor.getDepartamento(),
//                professor.getSiape()
//        );
//    }
//
//    /**
//     * Cria um ProfessorRequest com dados personalizados.
//     */
//    protected ProfessorRequest criarProfessorRequest(
//            String nome, String email, String senha, String cpf, String departamento, String siape) {
//        return new ProfessorRequest(nome, email, senha, cpf, departamento, siape);
//    }
//
//    /**
//     * Cria um UserLogin padrão para os testes.
//     */
//    protected UserLogin criarUserLoginPadrao(Professor professor) {
//        UserLogin userLogin = new UserLogin();
//        userLogin.setUsuario(professor);
//        userLogin.setEmail(professor.getEmail());
//        userLogin.setSenha(professor.getSenha());
//        userLogin.setAtivo(true);
//        return userLogin;
//    }
//
//    /**
//     * Cria um Professor ativo para testes.
//     */
//    protected Professor criarProfessorAtivo() {
//        Professor professor = criarProfessorPadrao();
//        professor.setAtivo(true);
//        return professor;
//    }
//
//    /**
//     * Cria um Professor inativo para testes.
//     */
//    protected Professor criarProfessorInativo() {
//        Professor professor = criarProfessorPadrao();
//        professor.setAtivo(false);
//        return professor;
//    }
//
//    /**
//     * Cria um UserLogin ativo para testes.
//     */
//    protected UserLogin criarUserLoginAtivo(Professor professor) {
//        UserLogin userLogin = criarUserLoginPadrao(professor);
//        userLogin.setAtivo(true);
//        return userLogin;
//    }
//
//    /**
//     * Cria um UserLogin inativo para testes.
//     */
//    protected UserLogin criarUserLoginInativo(Professor professor) {
//        UserLogin userLogin = criarUserLoginPadrao(professor);
//        userLogin.setAtivo(false);
//        return userLogin;
//    }
//
//    /**
//     * Cria um ProfessorRequest padrão para testes.
//     */
//    protected ProfessorRequest criarProfessorRequestPadrao() {
//        return new ProfessorRequest(
//                "Lucas Felipe",
//                "lucas@email.com",
//                "Senha@123",
//                "98765432100",
//                "Computação",
//                "1234567"
//        );
//    }
//
//}
