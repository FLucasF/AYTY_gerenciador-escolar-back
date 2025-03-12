package br.com.ufpb.GerenciadorEscolar.service.aluno;

import br.com.ufpb.GerenciadorEscolar.model.dto.aluno.AlunoResponse;
import br.com.ufpb.GerenciadorEscolar.model.entity.Aluno;
import br.com.ufpb.GerenciadorEscolar.service.AlunoNaoEncontradoException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlunoServiceImplBuscarTest extends BaseAlunoServiceTest {

    @Test
    void deveRetornarAluno_SeEncontrado() {
        // Arrange
        Aluno aluno = criarAlunoPadrao();
        AlunoResponse alunoResponse = criarAlunoResponse(aluno);

        when(alunoRepository.findByIdAndAtivoTrue(aluno.getId())).thenReturn(Optional.of(aluno));
        when(alunoMapper.toResponse(aluno)).thenReturn(alunoResponse);

        // Act
        AlunoResponse response = alunoService.buscarAlunoPorId(aluno.getId());

        // Assert
        assertNotNull(response);
        assertEquals(alunoResponse, response);

        verify(alunoRepository, times(1)).findByIdAndAtivoTrue(aluno.getId());
        verify(alunoMapper, times(1)).toResponse(aluno);
    }

    @Test
    void deveLancarExcecao_SeAlunoNaoForEncontrado() {
        // Arrange
        Long idInvalido = 999L;
        when(alunoRepository.findByIdAndAtivoTrue(idInvalido)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AlunoNaoEncontradoException.class, () -> alunoService.buscarAlunoPorId(idInvalido));

        verify(alunoRepository, times(1)).findByIdAndAtivoTrue(idInvalido);
        verify(alunoMapper, never()).toResponse(any());
    }
}
