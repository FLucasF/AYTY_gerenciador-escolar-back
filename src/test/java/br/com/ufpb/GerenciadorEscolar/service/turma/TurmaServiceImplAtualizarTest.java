package br.com.ufpb.GerenciadorEscolar.service.turma;

import br.com.ufpb.GerenciadorEscolar.dto.turma.TurmaRequest;
import br.com.ufpb.GerenciadorEscolar.dto.turma.TurmaResponse;
import br.com.ufpb.GerenciadorEscolar.model.Professor;
import br.com.ufpb.GerenciadorEscolar.model.Turma;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TurmaServiceImplAtualizarTest extends BaseTurmaServiceTest {

    // ✅ **Atualizar apenas o nome da turma**
    @Test
    public void testAtualizarSomenteNome() {
        Turma turma = criarTurmaPadrao();
        TurmaRequest request = criarTurmaRequest("Nome Atualizado", turma.getCodigo(), turma.getSemestre(), null);

        when(turmaRepository.findById(turma.getId())).thenReturn(Optional.of(turma));
        when(turmaRepository.saveAndFlush(any())).thenReturn(turma);
        when(turmaMapper.toResponse(any())).thenReturn(criarTurmaResponse(turma));

        TurmaResponse resultado = turmaService.atualizarTurma(turma.getId(), request);

        assertEquals("Nome Atualizado", resultado.nome());
        assertEquals(turma.getCodigo(), resultado.codigo());
        assertEquals(turma.getSemestre(), resultado.semestre());

        verify(turmaRepository, times(1)).saveAndFlush(turma);
    }

    // ✅ **Atualizar apenas o código da turma**
    @Test
    public void testAtualizarSomenteCodigo() {
        Turma turma = criarTurmaPadrao();
        TurmaRequest request = criarTurmaRequest(turma.getNome(), "NOVO123", turma.getSemestre(), null);

        when(turmaRepository.findById(turma.getId())).thenReturn(Optional.of(turma));
        when(turmaRepository.saveAndFlush(any())).thenReturn(turma);
        when(turmaMapper.toResponse(any())).thenReturn(criarTurmaResponse(turma));

        TurmaResponse resultado = turmaService.atualizarTurma(turma.getId(), request);

        assertEquals(turma.getNome(), resultado.nome());
        assertEquals("NOVO123", resultado.codigo());
        assertEquals(turma.getSemestre(), resultado.semestre());

        verify(turmaRepository, times(1)).saveAndFlush(turma);
    }

    // ✅ **Atualizar apenas o semestre da turma**
    @Test
    public void testAtualizarSomenteSemestre() {
        Turma turma = criarTurmaPadrao();
        TurmaRequest request = criarTurmaRequest(turma.getNome(), turma.getCodigo(), "2025.1", null);

        when(turmaRepository.findById(turma.getId())).thenReturn(Optional.of(turma));
        when(turmaRepository.saveAndFlush(any())).thenReturn(turma);
        when(turmaMapper.toResponse(any())).thenReturn(criarTurmaResponse(turma));

        TurmaResponse resultado = turmaService.atualizarTurma(turma.getId(), request);

        assertEquals(turma.getNome(), resultado.nome());
        assertEquals(turma.getCodigo(), resultado.codigo());
        assertEquals("2025.1", resultado.semestre());

        verify(turmaRepository, times(1)).saveAndFlush(turma);
    }

    // ✅ **Atualizar apenas o professor da turma**
    @Test
    public void testAtualizarSomenteProfessor() {
        Turma turma = criarTurmaPadrao();
        Professor professor = criarProfessorPadrao();
        TurmaRequest request = criarTurmaRequest(turma.getNome(), turma.getCodigo(), turma.getSemestre(), professor.getId());

        when(turmaRepository.findById(turma.getId())).thenReturn(Optional.of(turma));
        when(professorRepository.findById(professor.getId())).thenReturn(Optional.of(professor));
        when(turmaRepository.saveAndFlush(any())).thenReturn(turma);
        when(turmaMapper.toResponse(any())).thenReturn(criarTurmaResponse(turma));

        TurmaResponse resultado = turmaService.atualizarTurma(turma.getId(), request);

        assertEquals(turma.getNome(), resultado.nome());
        assertEquals(turma.getCodigo(), resultado.codigo());
        assertEquals(turma.getSemestre(), resultado.semestre());
        assertEquals(professor.getId(), resultado.professorId());

        verify(professorRepository, times(1)).findById(professor.getId());
        verify(turmaRepository, times(1)).saveAndFlush(turma);
    }

    // ✅ **Remover professor da turma**
    @Test
    public void testRemoverProfessorDaTurma() {
        Turma turma = criarTurmaPadrao();
        Professor professor = criarProfessorPadrao();
        turma.setProfessor(professor);

        TurmaRequest request = criarTurmaRequest(turma.getNome(), turma.getCodigo(), turma.getSemestre(), null);

        when(turmaRepository.findById(turma.getId())).thenReturn(Optional.of(turma));
        when(turmaRepository.saveAndFlush(any())).thenReturn(turma);
        when(turmaMapper.toResponse(any())).thenReturn(criarTurmaResponse(turma));

        TurmaResponse resultado = turmaService.atualizarTurma(turma.getId(), request);

        assertEquals(turma.getNome(), resultado.nome());
        assertEquals(turma.getCodigo(), resultado.codigo());
        assertEquals(turma.getSemestre(), resultado.semestre());
        assertNull(resultado.professorId());

        verify(professorRepository, never()).findById(any());
        verify(turmaRepository, times(1)).saveAndFlush(turma);
    }

    // ✅ **Tentativa de atualizar turma inexistente**
    @Test
    public void testAtualizarTurmaNaoEncontrada() {
        Long turmaId = 99L;
        TurmaRequest request = criarTurmaRequest("Turma Nova", "NOVO123", "2025.1", null);

        when(turmaRepository.findById(turmaId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                turmaService.atualizarTurma(turmaId, request));

        assertEquals("Turma não encontrada", exception.getMessage());

        verify(turmaRepository, never()).saveAndFlush(any());
    }

    // ✅ **Tentativa de atualizar turma com professor inexistente**
    @Test
    public void testAtualizarTurmaProfessorNaoEncontrado() {
        Turma turma = criarTurmaPadrao();
        Long professorId = 99L;
        TurmaRequest request = criarTurmaRequest(turma.getNome(), turma.getCodigo(), turma.getSemestre(), professorId);

        when(turmaRepository.findById(turma.getId())).thenReturn(Optional.of(turma));
        when(professorRepository.findById(professorId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                turmaService.atualizarTurma(turma.getId(), request));

        assertEquals("Professor não encontrado", exception.getMessage());

        verify(turmaRepository, never()).saveAndFlush(any());
    }
}
