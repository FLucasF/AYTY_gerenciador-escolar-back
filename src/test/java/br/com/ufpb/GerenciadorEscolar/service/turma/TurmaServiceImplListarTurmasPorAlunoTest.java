package br.com.ufpb.GerenciadorEscolar.service.turma;

import br.com.ufpb.GerenciadorEscolar.model.dto.turma.TurmaResponse;
import br.com.ufpb.GerenciadorEscolar.model.entity.Turma;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TurmaServiceImplListarTurmasPorAlunoTest extends BaseTurmaServiceTest {

    @Test
    void testListarTurmasPorAluno_ComSucesso() {
        // Arrange
        Long alunoId = 1L;
        Pageable pageable = PageRequest.of(0, 2); // Primeira página, 2 itens por página

        Turma turma1 = criarTurmaPadrao();

        Turma turma2 = new Turma();
        turma2.setId(2L);
        turma2.setNome("Turma B");
        turma2.setCodigo("COD002");
        turma2.setSemestre("2024.1");
        turma2.setAtivo(true);

        TurmaResponse response1 = criarTurmaResponse(turma1);
        TurmaResponse response2 = criarTurmaResponse(turma2);

        Page<Turma> turmasPage = new PageImpl<>(List.of(turma1, turma2), pageable, 2);

        when(turmaRepository.findByAlunosIdAndAtivoTrue(alunoId, pageable)).thenReturn(turmasPage);
        when(turmaMapper.toResponse(turma1)).thenReturn(response1);
        when(turmaMapper.toResponse(turma2)).thenReturn(response2);

        // Act
        Page<TurmaResponse> resultado = turmaService.listarTurmasPorAluno(alunoId, pageable);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.getTotalElements());
        assertEquals(2, resultado.getContent().size());
        assertEquals("Turma A", resultado.getContent().get(0).nome());
        assertEquals("Turma B", resultado.getContent().get(1).nome());

        // Verificações dos mocks
        verify(turmaRepository, times(1)).findByAlunosIdAndAtivoTrue(alunoId, pageable);
        verify(turmaMapper, times(2)).toResponse(any(Turma.class));
    }


    @Test
    void testListarTurmasPorAluno_SemTurmas() {
        // Arrange
        Long alunoId = 1L;
        Pageable pageable = PageRequest.of(0, 2);

        Page<Turma> turmasVazias = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(turmaRepository.findByAlunosIdAndAtivoTrue(alunoId, pageable)).thenReturn(turmasVazias);

        // Act
        Page<TurmaResponse> resultado = turmaService.listarTurmasPorAluno(alunoId, pageable);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.getContent().isEmpty());

        // Verificações dos mocks
        verify(turmaRepository, times(1)).findByAlunosIdAndAtivoTrue(alunoId, pageable);
        verify(turmaMapper, never()).toResponse(any(Turma.class));
    }

    @Test
    void testListarTurmasPorAluno_AlunoIdNulo() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 2);

        // Act & Assert
        Exception exception = assertThrows(NullPointerException.class, () ->
                turmaService.listarTurmasPorAluno(null, pageable)
        );

        assertEquals("ID do aluno não pode ser nulo", exception.getMessage());
        verify(turmaRepository, never()).findByAlunosIdAndAtivoTrue(any(), any());
    }

    @Test
    void testListarTurmasPorAluno_AlunoIdNegativo() {
        // Arrange
        Long alunoId = -1L;
        Pageable pageable = PageRequest.of(0, 2);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                turmaService.listarTurmasPorAluno(alunoId, pageable)
        );

        assertEquals("ID do aluno não pode ser negativo", exception.getMessage());
        verify(turmaRepository, never()).findByAlunosIdAndAtivoTrue(any(), any());
    }

    @Test
    void testListarTurmasPorAluno_PaginaForaDoIntervalo() {
        // Arrange
        Long alunoId = 1L;
        Pageable pageable = PageRequest.of(100, 2); // Página muito alta

        Page<Turma> turmasVazia = new PageImpl<>(List.of(), pageable, 2);

        when(turmaRepository.findByAlunosIdAndAtivoTrue(alunoId, pageable)).thenReturn(turmasVazia);

        // Act
        Page<TurmaResponse> resultado = turmaService.listarTurmasPorAluno(alunoId, pageable);

        // Assert
        assertNotNull(resultado);
        assertEquals(0, resultado.getContent().size());

        verify(turmaRepository, times(1)).findByAlunosIdAndAtivoTrue(alunoId, pageable);
    }


}
