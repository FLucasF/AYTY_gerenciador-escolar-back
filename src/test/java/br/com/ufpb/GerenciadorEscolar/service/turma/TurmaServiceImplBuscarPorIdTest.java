package br.com.ufpb.GerenciadorEscolar.service.turma;

import br.com.ufpb.GerenciadorEscolar.dto.turma.TurmaResponse;
import br.com.ufpb.GerenciadorEscolar.model.Turma;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
public class TurmaServiceImplBuscarPorIdTest extends BaseTurmaServiceTest {

    @Test
    public void testBuscarTurmaPorId_Success() {
        Long turmaId = 1L;
        Turma turma = criarTurmaPadrao();
        TurmaResponse turmaResponse = criarTurmaResponse(turma);

        when(turmaRepository.findById(turmaId)).thenReturn(Optional.of(turma));
        when(turmaMapper.toResponse(turma)).thenReturn(turmaResponse);

        Optional<TurmaResponse> resultado = turmaService.buscarTurmaPorId(turmaId);

        assertTrue(resultado.isPresent(), "A turma deveria estar presente!");
        assertEquals("Turma A", resultado.get().nome());

        verify(turmaRepository, times(1)).findById(turmaId);
        verify(turmaMapper, times(1)).toResponse(turma);
    }

    @Test
    public void testBuscarTurmaPorId_NotFound() {
        Long turmaId = 99L;

        when(turmaRepository.findById(turmaId)).thenReturn(Optional.empty());

        Optional<TurmaResponse> resultado = turmaService.buscarTurmaPorId(turmaId);

        assertFalse(resultado.isPresent(), "Esperava-se que a turma não fosse encontrada!");
        verify(turmaRepository, times(1)).findById(turmaId);
    }

    @Test
    public void testBuscarTurmaPorId_NullId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> turmaService.buscarTurmaPorId(null));

        assertEquals("ID da turma não pode ser nulo", exception.getMessage());
        verify(turmaRepository, never()).findById(any());
    }

    @Test
    public void testBuscarTurmaPorId_NegativeId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> turmaService.buscarTurmaPorId(-1L));

        assertEquals("ID da turma não pode ser negativo", exception.getMessage());
        verify(turmaRepository, never()).findById(any());
    }
}
