package br.com.ufpb.GerenciadorEscolar.service.turma;

import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoResponse;
import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.model.Turma;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TurmaServiceImplListarAlunosPorTurmaTest extends BaseTurmaServiceTest {

    @Test
    void testListarAlunosPorTurma_Success() {
        Long turmaId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        Turma turma = criarTurmaPadrao();
        Aluno aluno1 = criarAlunoPadrao();
        Aluno aluno2 = new Aluno();
        aluno2.setId(2L);
        aluno2.setNome("Aluno 2");

        Page<Aluno> alunoPage = new PageImpl<>(List.of(aluno1, aluno2), pageable, 2);

        when(turmaRepository.existsById(turmaId)).thenReturn(true);
        when(alunoRepository.findByTurmasId(turmaId, pageable)).thenReturn(alunoPage);

        // ✅ Corrigindo a criação do AlunoResponse com os 5 argumentos exigidos
        when(alunoMapper.toResponse(aluno1)).thenReturn(
                new AlunoResponse(1L, "Aluno 1", "aluno1@email.com", "12345678900", "Engenharia")
        );
        when(alunoMapper.toResponse(aluno2)).thenReturn(
                new AlunoResponse(2L, "Aluno 2", "aluno2@email.com", "98765432100", "Medicina")
        );

        Page<AlunoResponse> resultado = turmaService.listarAlunosPorTurma(turmaId, pageable);

        assertNotNull(resultado);
        assertEquals(2, resultado.getTotalElements());
        assertEquals("Aluno 1", resultado.getContent().get(0).nome());
        assertEquals("Aluno 2", resultado.getContent().get(1).nome());

        verify(turmaRepository, times(1)).existsById(turmaId);
        verify(alunoRepository, times(1)).findByTurmasId(turmaId, pageable);
        verify(alunoMapper, times(2)).toResponse(any(Aluno.class));
    }


    @Test
    void testListarAlunosPorTurma_TurmaSemAlunos() {
        Long turmaId = 2L;
        Pageable pageable = PageRequest.of(0, 10);

        when(turmaRepository.existsById(turmaId)).thenReturn(true);
        when(alunoRepository.findByTurmasId(turmaId, pageable)).thenReturn(Page.empty());

        Page<AlunoResponse> resultado = turmaService.listarAlunosPorTurma(turmaId, pageable);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(turmaRepository, times(1)).existsById(turmaId);
        verify(alunoRepository, times(1)).findByTurmasId(turmaId, pageable);
    }

    @Test
    void testListarAlunosPorTurma_TurmaIdNulo() {
        Pageable pageable = PageRequest.of(0, 10);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                turmaService.listarAlunosPorTurma(null, pageable)
        );

        assertEquals("ID da turma não pode ser nulo", exception.getMessage());
        verify(turmaRepository, never()).existsById(any());
    }

    @Test
    void testListarAlunosPorTurma_TurmaIdNegativo() {
        Pageable pageable = PageRequest.of(0, 10);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                turmaService.listarAlunosPorTurma(-1L, pageable)
        );

        assertEquals("ID da turma não pode ser negativo", exception.getMessage());
        verify(turmaRepository, never()).existsById(any());
    }

    @Test
    void testListarAlunosPorTurma_TurmaNaoExiste() {
        Long turmaId = 99L;
        Pageable pageable = PageRequest.of(0, 10);

        when(turmaRepository.existsById(turmaId)).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () ->
                turmaService.listarAlunosPorTurma(turmaId, pageable)
        );

        assertEquals("Turma não encontrada", exception.getMessage());
        verify(turmaRepository, times(1)).existsById(turmaId);
        verify(alunoRepository, never()).findByTurmasId(anyLong(), any());
    }

    @Test
    void testListarAlunosPorTurma_Paginacao() {
        Long turmaId = 1L;
        Pageable pageable = PageRequest.of(0, 1); // Apenas um aluno por página

        Turma turma = criarTurmaPadrao();
        Aluno aluno1 = criarAlunoPadrao();

        Page<Aluno> alunoPage = new PageImpl<>(List.of(aluno1), pageable, 2);

        when(turmaRepository.existsById(turmaId)).thenReturn(true);
        when(alunoRepository.findByTurmasId(turmaId, pageable)).thenReturn(alunoPage);

        // ✅ Corrigindo a criação do AlunoResponse com os 5 argumentos exigidos
        when(alunoMapper.toResponse(aluno1)).thenReturn(
                new AlunoResponse(1L, "Aluno 1", "aluno1@email.com", "12345678900", "Engenharia")
        );

        Page<AlunoResponse> resultado = turmaService.listarAlunosPorTurma(turmaId, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getSize());
        assertEquals(2, resultado.getTotalElements());
        assertEquals("Aluno 1", resultado.getContent().get(0).nome());

        verify(turmaRepository, times(1)).existsById(turmaId);
        verify(alunoRepository, times(1)).findByTurmasId(turmaId, pageable);
        verify(alunoMapper, times(1)).toResponse(any(Aluno.class));
    }

}
