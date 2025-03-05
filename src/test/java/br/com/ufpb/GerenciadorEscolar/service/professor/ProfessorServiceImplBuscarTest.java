package br.com.ufpb.GerenciadorEscolar.service.professor;

import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorResponse;
import br.com.ufpb.GerenciadorEscolar.model.Professor;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProfessorServiceImplBuscarTest extends BaseProfessorServiceTest {

    // ✅ Teste: Professor encontrado com sucesso
    @Test
    public void testBuscarProfessorPorId_Found() {
        Long id = 1L;
        Professor professor = new Professor();
        professor.setId(id);

        ProfessorResponse response = new ProfessorResponse(
                professor.getId(), "Nome do Professor", "email@teste.com", "12345678901", "Departamento", "1234567"
        );

        when(professorRepository.findByIdAndAtivoTrue(id)).thenReturn(Optional.of(professor));
        when(professorMapper.toResponse(professor)).thenReturn(response);

        Optional<ProfessorResponse> result = professorService.buscarProfessorPorId(id);

        assertTrue(result.isPresent());
        assertEquals(response, result.get());
        verify(professorRepository, times(1)).findByIdAndAtivoTrue(id);
    }

    // ✅ Teste: Professor não encontrado (retorna Optional.empty)
    @Test
    public void testBuscarProfessorPorId_NotFound() {
        Long id = 1L;

        when(professorRepository.findByIdAndAtivoTrue(id)).thenReturn(Optional.empty());

        Optional<ProfessorResponse> result = professorService.buscarProfessorPorId(id);

        assertFalse(result.isPresent());
        verify(professorRepository, times(1)).findByIdAndAtivoTrue(id);
    }

    // ❌ Teste: ID nulo deve lançar exceção
    @Test
    public void testBuscarProfessorPorId_NullId() {
        Exception exception = assertThrows(NullPointerException.class, () ->
                professorService.buscarProfessorPorId(null)
        );

        assertEquals("ID não pode ser nulo", exception.getMessage());
        verify(professorRepository, never()).findByIdAndAtivoTrue(any());
    }

    // ❌ Teste: ID negativo deve lançar exceção
    @Test
    public void testBuscarProfessorPorId_NegativeId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                professorService.buscarProfessorPorId(-1L)
        );

        assertEquals("ID não pode ser nulo ou inválido", exception.getMessage());
        verify(professorRepository, never()).findByIdAndAtivoTrue(any());
    }
}
