package br.com.ufpb.GerenciadorEscolar.service.turma;

import br.com.ufpb.GerenciadorEscolar.model.dto.turma.TurmaResponse;
import br.com.ufpb.GerenciadorEscolar.model.entity.Turma;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TurmaServiceImplListarTurmasPorProfessorTest extends BaseTurmaServiceTest {

    @Test
    void testListarTurmasPorProfessor_Sucesso() {
        Long professorId = 1L;
        Pageable pageable = PageRequest.of(0, 2);
        Turma turma1 = criarTurmaPadrao();
        Turma turma2 = new Turma();
        turma2.setId(2L);
        turma2.setNome("Turma B");
        turma2.setCodigo("COD002");
        turma2.setSemestre("2024.1");
        turma2.setAtivo(true);

        List<Turma> listaTurmas = List.of(turma1, turma2);
        Page<Turma> paginaTurmas = new PageImpl<>(listaTurmas, pageable, listaTurmas.size());

        when(turmaRepository.findByProfessorIdAndAtivoTrue(professorId, pageable)).thenReturn(paginaTurmas);
        when(turmaMapper.toResponse(turma1)).thenReturn(criarTurmaResponse(turma1));
        when(turmaMapper.toResponse(turma2)).thenReturn(criarTurmaResponse(turma2));

        Page<TurmaResponse> resultado = turmaService.listarTurmasPorProfessor(professorId, pageable);

        assertNotNull(resultado);
        assertEquals(2, resultado.getContent().size());
        assertEquals("Turma A", resultado.getContent().get(0).nome());
        assertEquals("Turma B", resultado.getContent().get(1).nome());

        verify(turmaRepository, times(1)).findByProfessorIdAndAtivoTrue(professorId, pageable);
        verify(turmaMapper, times(2)).toResponse(any(Turma.class));
    }

    @Test
    void testListarTurmasPorProfessor_SemResultados() {
        Long professorId = 1L;
        Pageable pageable = PageRequest.of(0, 2);
        Page<Turma> paginaVazia = Page.empty(pageable);

        when(turmaRepository.findByProfessorIdAndAtivoTrue(professorId, pageable)).thenReturn(paginaVazia);

        Page<TurmaResponse> resultado = turmaService.listarTurmasPorProfessor(professorId, pageable);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(turmaRepository, times(1)).findByProfessorIdAndAtivoTrue(professorId, pageable);
        verifyNoInteractions(turmaMapper);
    }

    @Test
    void testListarTurmasPorProfessor_PaginacaoMaiorQueResultados() {
        Long professorId = 1L;
        Pageable pageable = PageRequest.of(10, 5); // Página 10, que não existe
        Page<Turma> paginaVazia = Page.empty(pageable);

        when(turmaRepository.findByProfessorIdAndAtivoTrue(professorId, pageable)).thenReturn(paginaVazia);

        Page<TurmaResponse> resultado = turmaService.listarTurmasPorProfessor(professorId, pageable);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(turmaRepository, times(1)).findByProfessorIdAndAtivoTrue(professorId, pageable);
    }

    @Test
    void testListarTurmasPorProfessor_PaginacaoComMaisTurmasQuePaginaSuporta() {
        Long professorId = 1L;
        Pageable pageable = PageRequest.of(0, 1); // Página 0, mas apenas 1 turma por página
        Turma turma1 = criarTurmaPadrao();
        List<Turma> listaTurmas = List.of(turma1);
        Page<Turma> paginaTurmas = new PageImpl<>(listaTurmas, pageable, 3); // Simula que há 3 no total

        when(turmaRepository.findByProfessorIdAndAtivoTrue(professorId, pageable)).thenReturn(paginaTurmas);
        when(turmaMapper.toResponse(turma1)).thenReturn(criarTurmaResponse(turma1));

        Page<TurmaResponse> resultado = turmaService.listarTurmasPorProfessor(professorId, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size()); // Deve retornar só 1 porque é o tamanho da página
        assertEquals("Turma A", resultado.getContent().get(0).nome());

        verify(turmaRepository, times(1)).findByProfessorIdAndAtivoTrue(professorId, pageable);
        verify(turmaMapper, times(1)).toResponse(any(Turma.class));
    }

    @Test
    void testListarTurmasPorProfessor_ParametrosInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> {
            Pageable pageable = PageRequest.of(-1, 2); // Página negativa
            turmaService.listarTurmasPorProfessor(1L, pageable);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Pageable pageable = PageRequest.of(0, -5); // Tamanho da página negativo
            turmaService.listarTurmasPorProfessor(1L, pageable);
        });

        assertThrows(NullPointerException.class, () -> {
            turmaService.listarTurmasPorProfessor(null, PageRequest.of(0, 2)); // Professor ID nulo
        });
    }
}
