package br.com.ufpb.GerenciadorEscolar.service.aluno;

import br.com.ufpb.GerenciadorEscolar.mapper.AlunoMapper;
import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.repository.AlunoRepository;
import br.com.ufpb.GerenciadorEscolar.service.AlunoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.when;


public abstract class BaseAlunoServiceTest {

    @Mock
    protected AlunoRepository alunoRepository;

    @Mock
    protected PasswordEncoder passwordEncoder;

    @Mock
    protected AlunoMapper alunoMapper;

    @InjectMocks
    protected AlunoServiceImpl alunoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    protected void mockFindById(Long id, Aluno aluno) {
        when(alunoRepository.findByIdAndAtivoTrue(id)).thenReturn(Optional.ofNullable(aluno));
    }

    protected void mockSave(Aluno aluno) {
        when(alunoRepository.save(aluno)).thenReturn(aluno);
    }
}
