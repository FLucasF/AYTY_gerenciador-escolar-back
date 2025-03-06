package br.com.ufpb.GerenciadorEscolar.service.professor;

import br.com.ufpb.GerenciadorEscolar.model.Professor;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProfessorServiceImplFindByEmailTest extends BaseProfessorServiceTest {

    // ✅ Cenário 1: Professor encontrado com sucesso
    @Test
    public void testFindByEmail_Success() {
        Professor professor = criarProfessorPadrao();

        // Simulando o retorno do repositório com um professor ativo
        when(professorRepository.findByEmailAndAtivoTrue(professor.getEmail())).thenReturn(Optional.of(professor));

        // Chama o método que estamos testando
        Optional<Professor> result = professorService.findByEmail(professor.getEmail());

        // Verifica se o resultado não é vazio e se o professor retornado é o esperado
        assertTrue(result.isPresent(), "Professor não encontrado.");
        assertEquals(professor.getEmail(), result.get().getEmail(), "O e-mail não corresponde ao professor esperado.");

        // Verifica se o repositório foi chamado uma vez
        verify(professorRepository, times(1)).findByEmailAndAtivoTrue(professor.getEmail());
    }

    // ✅ Cenário 2: Professor não encontrado
    @Test
    public void testFindByEmail_ProfessorNaoEncontrado() {
        String email = "professor@teste.com";

        // Simula que o professor não é encontrado no banco
        when(professorRepository.findByEmailAndAtivoTrue(email)).thenReturn(Optional.empty());

        // Chama o método
        Optional<Professor> result = professorService.findByEmail(email);

        // Verifica se o resultado é vazio
        assertFalse(result.isPresent(), "Professor encontrado quando não deveria.");

        // Verifica se o repositório foi chamado uma vez
        verify(professorRepository, times(1)).findByEmailAndAtivoTrue(email);
    }

    // ✅ Cenário 3: Email nulo
    @Test
    public void testFindByEmail_EmailNulo() {
        Exception exception = assertThrows(NullPointerException.class, () -> {
            professorService.findByEmail(null);
        });

        assertEquals("Email não pode ser nulo", exception.getMessage());
    }

    // ✅ Cenário 4: Email vazio
    @Test
    public void testFindByEmail_EmailVazio() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            professorService.findByEmail("");
        });

        assertEquals("Email não pode ser vazio", exception.getMessage());
    }
}
