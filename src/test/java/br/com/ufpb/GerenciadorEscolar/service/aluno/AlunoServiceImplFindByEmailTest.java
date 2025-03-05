package br.com.ufpb.GerenciadorEscolar.service.aluno;

import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.model.Professor;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AlunoServiceImplFindByEmailTest extends BaseAlunoServiceTest {

    // ✅ Cenário 1: Professor encontrado com sucesso
    @Test
    public void testFindByEmail_Success() {
        Aluno aluno = criarAlunoPadrao();

        // Simulando o retorno do repositório com um professor ativo
        when(alunoRepository.findByEmailAndAtivoTrue(aluno.getEmail())).thenReturn(Optional.of(aluno));

        // Chama o método que estamos testando
        Optional<Aluno> result = alunoService.findByEmail(aluno.getEmail());

        // Verifica se o resultado não é vazio e se o professor retornado é o esperado
        assertTrue(result.isPresent(), "Professor não encontrado.");
        assertEquals(aluno.getEmail(), result.get().getEmail(), "O e-mail não corresponde ao professor esperado.");

        // Verifica se o repositório foi chamado uma vez
        verify(alunoRepository, times(1)).findByEmailAndAtivoTrue(aluno.getEmail());
    }

    // ✅ Cenário 2: Professor não encontrado
    @Test
    public void testFindByEmail_ProfessorNaoEncontrado() {
        String email = "professor@teste.com";

        // Simula que o professor não é encontrado no banco
        when(alunoRepository.findByEmailAndAtivoTrue(email)).thenReturn(Optional.empty());

        // Chama o método
        Optional<Aluno> result = alunoService.findByEmail(email);

        // Verifica se o resultado é vazio
        assertFalse(result.isPresent(), "Professor encontrado quando não deveria.");

        // Verifica se o repositório foi chamado uma vez
        verify(alunoRepository, times(1)).findByEmailAndAtivoTrue(email);
    }

    // ✅ Cenário 3: Email nulo
    @Test
    public void testFindByEmail_EmailNulo() {
        Exception exception = assertThrows(NullPointerException.class, () -> {
            alunoService.findByEmail(null);
        });

        assertEquals("Email não pode ser nulo", exception.getMessage());
    }

    // ✅ Cenário 4: Email vazio
    @Test
    public void testFindByEmail_EmailVazio() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            alunoService.findByEmail("");
        });

        assertEquals("Email não pode ser vazio", exception.getMessage());
    }
}
