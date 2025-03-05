package br.com.ufpb.GerenciadorEscolar;

import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.repository.AlunoRepository;
import br.com.ufpb.GerenciadorEscolar.mapper.AlunoMapper;
import br.com.ufpb.GerenciadorEscolar.service.AlunoServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AlunoServiceImplFindByEmailTest {

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private AlunoMapper alunoMapper;

    @InjectMocks
    private AlunoServiceImpl alunoService;

    @Test
    public void testFindByEmail_Success() {
        String email = "email@teste.com";
        Aluno aluno = new Aluno();
        aluno.setId(1L);
        aluno.setEmail(email);
        aluno.setNome("Nome do Aluno");
        aluno.setCpf("12345678901");
        aluno.setCurso("Curso A");
        aluno.setAtivo(true);

        // Simulando o retorno do repositório com um aluno ativo
        when(alunoRepository.findByEmailAndAtivoTrue(email)).thenReturn(Optional.of(aluno));

        // Chama o método que estamos testando
        Optional<Aluno> result = alunoService.findByEmail(email);

        // Verifica se o resultado não é vazio e se o aluno retornado é o esperado
        assertTrue(result.isPresent(), "Aluno não encontrado.");
        assertEquals(aluno.getEmail(), result.get().getEmail(), "O e-mail não corresponde ao aluno esperado.");

        // Verifica se o repositório foi chamado uma vez
        verify(alunoRepository, times(1)).findByEmailAndAtivoTrue(email);
    }

    @Test
    public void testFindByEmail_AlunoNaoEncontrado() {
        String email = "email@teste.com";

        // Simula que o aluno não é encontrado no banco
        when(alunoRepository.findByEmailAndAtivoTrue(email)).thenReturn(Optional.empty());

        // Chama o método
        Optional<Aluno> result = alunoService.findByEmail(email);

        // Verifica se o resultado é vazio
        assertFalse(result.isPresent(), "Aluno encontrado quando não deveria.");

        // Verifica se o repositório foi chamado uma vez
        verify(alunoRepository, times(1)).findByEmailAndAtivoTrue(email);
    }

    @Test
    public void testFindByEmail_EmailNulo() {
        // Testa se o método lança exceção quando o e-mail é nulo
        String email = null;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            alunoService.findByEmail(email);
        });

        assertEquals("Email não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    public void testFindByEmail_EmailVazio() {
        String email = "";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            alunoService.findByEmail(email);
        });

        assertEquals("Email não pode ser nulo ou vazio", exception.getMessage());
    }
}
