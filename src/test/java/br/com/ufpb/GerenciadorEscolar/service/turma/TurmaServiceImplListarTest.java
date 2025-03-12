package br.com.ufpb.GerenciadorEscolar.service.turma;

import br.com.ufpb.GerenciadorEscolar.model.dto.turma.TurmaResponse;
import br.com.ufpb.GerenciadorEscolar.model.entity.Turma;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TurmaServiceImplListarTest extends BaseTurmaServiceTest {

    // ✅ Testa listagem de turmas com sucesso
    @Test
    public void testListarTodasTurmas_Success() {
        PageRequest pageable = PageRequest.of(0, 10);
        Turma turma = criarTurmaPadrao();
        TurmaResponse turmaResponse = criarTurmaResponse(turma);

        Page<Turma> turmaPage = new PageImpl<>(List.of(turma), pageable, 1);
        when(turmaRepository.findAll(pageable)).thenReturn(turmaPage);
        when(turmaMapper.toResponse(turma)).thenReturn(turmaResponse);

        Page<TurmaResponse> resultado = turmaService.listarTodasTurmas(pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(turmaResponse, resultado.getContent().get(0));

        verify(turmaRepository, times(1)).findAll(pageable);
        verify(turmaMapper, times(1)).toResponse(turma);
    }

    // ✅ Testa quando não há turmas cadastradas
    @Test
    public void testListarTodasTurmas_EmptyList() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Turma> emptyPage = new PageImpl<>(Collections.emptyList());

        when(turmaRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<TurmaResponse> resultado = turmaService.listarTodasTurmas(pageable);

        assertNotNull(resultado);
        assertEquals(0, resultado.getTotalElements());
        assertTrue(resultado.getContent().isEmpty());

        verify(turmaRepository, times(1)).findAll(pageable);
        verify(turmaMapper, never()).toResponse(any());
    }

    // ✅ Simula um erro no banco de dados
    @Test
    public void testListarTodasTurmas_DatabaseFailure() {
        PageRequest pageable = PageRequest.of(0, 10);

        when(turmaRepository.findAll(pageable))
                .thenThrow(new RuntimeException("Erro ao acessar banco de dados"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                turmaService.listarTodasTurmas(pageable)
        );

        assertEquals("Erro ao acessar banco de dados", exception.getMessage());
        verify(turmaRepository, times(1)).findAll(pageable);
    }

    // ❌ **Teste: Falha ao converter entidade para DTO** → Deve falhar
    @Test
    public void testListarTodasTurmas_FailureOnMapping() {
        PageRequest pageable = PageRequest.of(0, 10);
        Turma turma = criarTurmaPadrao();

        Page<Turma> turmaPage = new PageImpl<>(List.of(turma), pageable, 1);
        when(turmaRepository.findAll(pageable)).thenReturn(turmaPage);

        // Simula erro no mapeamento para DTO
        when(turmaMapper.toResponse(turma))
                .thenThrow(new RuntimeException("Erro ao converter turma para DTO"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                turmaService.listarTodasTurmas(pageable)
        );

        assertEquals("Erro ao converter turma para DTO", exception.getMessage());
        verify(turmaRepository, times(1)).findAll(pageable);
    }
}
