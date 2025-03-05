package br.com.ufpb.GerenciadorEscolar.service.administrador;

import br.com.ufpb.GerenciadorEscolar.repository.AdministradorRepository;
import br.com.ufpb.GerenciadorEscolar.repository.UserLoginRepository;
import br.com.ufpb.GerenciadorEscolar.mapper.AdministradorMapper;
import br.com.ufpb.GerenciadorEscolar.service.AdministradorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

public abstract class BaseAdministradorServiceTest {

    @Mock
    protected AdministradorRepository administradorRepository;

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
                userLoginRepository // ✅ Adicionando corretamente
        );
    }
}
