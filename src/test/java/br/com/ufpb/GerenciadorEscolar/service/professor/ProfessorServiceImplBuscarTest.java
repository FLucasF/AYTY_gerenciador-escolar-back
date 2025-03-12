package br.com.ufpb.GerenciadorEscolar.service.professor;

import br.com.ufpb.GerenciadorEscolar.model.dto.professor.ProfessorResponse;
import br.com.ufpb.GerenciadorEscolar.model.entity.Professor;
import br.com.ufpb.GerenciadorEscolar.service.ProfessorNaoEncontradoException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfessorServiceImplBuscarTest extends BaseProfessorServiceTest {

    @Test
    void deveBuscarProfessorPorIdComSucesso() {
        // Arrange
        Professor professor = criarProfessorPadrao();
        ProfessorResponse professorResponse = criarProfessorResponse(professor);

        when(professorRepository.findByIdAndAtivoTrue(professor.getId())).thenReturn(Optional.of(professor));
        when(professorMapper.toResponse(professor)).thenReturn(professorResponse);

        // Act
        ProfessorResponse response = professorService.buscarProfessorPorId(professor.getId());

        // Assert
        assertNotNull(response);
        assertEquals(professor.getId(), response.id());
        assertEquals(professor.getNome(), response.nome());

        verify(professorRepository).findByIdAndAtivoTrue(professor.getId());
        verify(professorMapper).toResponse(professor);
    }

    @Test
    void deveLancarExcecao_SeProfessorNaoForEncontrado() {
        // Arrange
        Long idInexistente = 99L;
        when(professorRepository.findByIdAndAtivoTrue(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProfessorNaoEncontradoException.class, () -> professorService.buscarProfessorPorId(idInexistente));

        verify(professorRepository).findByIdAndAtivoTrue(idInexistente);
        verify(professorMapper, never()).toResponse(any());
    }

}
