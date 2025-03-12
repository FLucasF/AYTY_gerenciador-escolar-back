package br.com.ufpb.GerenciadorEscolar.service.administrador;

import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorRequest;
import br.com.ufpb.GerenciadorEscolar.mapper.AdministradorMapper;
import br.com.ufpb.GerenciadorEscolar.model.Administrador;
import br.com.ufpb.GerenciadorEscolar.model.UserLogin;
import br.com.ufpb.GerenciadorEscolar.repository.AdministradorRepository;
import br.com.ufpb.GerenciadorEscolar.repository.UserLoginRepository;
import br.com.ufpb.GerenciadorEscolar.service.AdministradorServiceImpl;
import br.com.ufpb.GerenciadorEscolar.util.UsuarioUtils;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

public abstract class BaseAdministradorServiceTest {

    @Mock
    protected AdministradorRepository administradorRepository;

    @Mock
    protected UsuarioUtils usuarioUtils;

    @Mock
    protected UserLoginRepository userLoginRepository;

    @Mock
    protected PasswordEncoder passwordEncoder;

    @Mock
    protected AdministradorMapper administradorMapper;

    protected AdministradorServiceImpl administradorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        administradorService = new AdministradorServiceImpl(
                administradorRepository,
                passwordEncoder,
                administradorMapper,
                userLoginRepository,
                usuarioUtils
        );
    }

    /**
     * Cria um Administrador padrão para testes.
     */
    protected Administrador criarAdministradorPadrao() {
        Administrador administrador = new Administrador();
        administrador.setId(1L);
        administrador.setNome("João Silva");
        administrador.setEmail("joao@email.com");
        administrador.setCpf("12345678901");
        administrador.setSetor("RH");
        administrador.setSiape("1234567");
        administrador.setSenha("Senha@123"); // Atualizado para obedecer às novas regras de senha
        return administrador;
    }

    /**
     * Cria um UserLogin padrão para testes.
     */
    protected UserLogin criarUserLoginPadrao(Administrador administrador) {
        UserLogin userLogin = new UserLogin();
        userLogin.setId(1L);
        userLogin.setEmail(administrador.getEmail());
        userLogin.setSenha(administrador.getSenha());
        userLogin.setUsuario(administrador);
        return userLogin;
    }

    /**
     * Cria um AdministradorRequest padrão para testes.
     */
    protected AdministradorRequest criarAdministradorRequestPadrao() {
        return new AdministradorRequest(
                "João Silva", "joao@email.com", "Senha@123",
                "12345678901", "RH", "1234567"
        );
    }

    /**
     * Cria um AdministradorRequest com valores personalizados.
     */
    protected AdministradorRequest criarAdministradorRequest(String nome, String email, String senha, String cpf) {
        return new AdministradorRequest(
                nome, email, senha, cpf, "RH", "1234567"
        );
    }
}
