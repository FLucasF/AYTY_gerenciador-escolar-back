package br.com.ufpb.GerenciadorEscolar.service.turma;

import br.com.ufpb.GerenciadorEscolar.dto.turma.TurmaRequest;
import br.com.ufpb.GerenciadorEscolar.dto.turma.TurmaResponse;
import br.com.ufpb.GerenciadorEscolar.mapper.TurmaMapper;
import br.com.ufpb.GerenciadorEscolar.model.Professor;
import br.com.ufpb.GerenciadorEscolar.model.Turma;
import br.com.ufpb.GerenciadorEscolar.repository.ProfessorRepository;
import br.com.ufpb.GerenciadorEscolar.repository.TurmaRepository;
import br.com.ufpb.GerenciadorEscolar.service.TurmaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TurmaServiceImplCriarTest extends BaseTurmaServiceTest {
    // ✅ **Caso de sucesso: Criar turma sem professor**
    @Test
    public void testCriarTurma_Success_SemProfessor() {
        TurmaRequest request = new TurmaRequest("Turma A", "TURMA123", "2024.1", null);
        Turma turma = new Turma();
        turma.setId(1L);
        turma.setNome("Turma A");
        turma.setCodigo("TURMA123");
        turma.setSemestre("2024.1");

        TurmaResponse response = new TurmaResponse(1L, "Turma A", "TURMA123", "2024.1", null);

        when(turmaMapper.toEntity(request)).thenReturn(turma);
        when(turmaRepository.save(turma)).thenReturn(turma);
        when(turmaMapper.toResponse(turma)).thenReturn(response);

        TurmaResponse resultado = turmaService.criarTurma(request);

        assertNotNull(resultado);
        assertEquals(response, resultado);
        verify(turmaRepository, times(1)).save(turma);
    }

    // ✅ **Caso de sucesso: Criar turma com professor associado**
    @Test
    public void testCriarTurma_Success_ComProfessor() {
        Professor professor = new Professor();
        professor.setId(1L);
        professor.setNome("Professor Teste");

        TurmaRequest request = new TurmaRequest("Turma B", "TURMA456", "2024.2", professor.getId());
        Turma turma = new Turma();
        turma.setId(2L);
        turma.setNome("Turma B");
        turma.setCodigo("TURMA456");
        turma.setSemestre("2024.2");
        turma.setProfessor(professor);

        TurmaResponse response = new TurmaResponse(2L, "Turma B", "TURMA456", "2024.2", professor.getId());

        when(professorRepository.findById(professor.getId())).thenReturn(Optional.of(professor));
        when(turmaMapper.toEntity(request)).thenReturn(turma);
        when(turmaRepository.save(turma)).thenReturn(turma);
        when(turmaMapper.toResponse(turma)).thenReturn(response);

        TurmaResponse resultado = turmaService.criarTurma(request);

        assertNotNull(resultado);
        assertEquals(professor.getId(), resultado.professorId());
        verify(professorRepository, times(1)).findById(professor.getId());
        verify(turmaRepository, times(1)).save(turma);
    }

    // ✅ **Erro: Criar turma com campo `null`**
    @Test
    public void testCriarTurma_Fail_NullFields() {
        TurmaRequest request = new TurmaRequest(null, "TURMA123", "2024.1", null);

        Exception exception = assertThrows(NullPointerException.class, () ->
                turmaService.criarTurma(request)
        );

        assertEquals("O campo Nome não pode ser nulo.", exception.getMessage());
        verify(turmaRepository, never()).save(any());
    }

    // ✅ **Erro: Criar turma com campo vazio**
    @Test
    public void testCriarTurma_Fail_EmptyFields() {
        TurmaRequest request = new TurmaRequest("", "TURMA123", "2024.1", null);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                turmaService.criarTurma(request)
        );

        assertEquals("O campo Nome não pode ser vazio.", exception.getMessage());
        verify(turmaRepository, never()).save(any());
    }

    // ✅ **Erro: Criar turma com professor inexistente**
    @Test
    public void testCriarTurma_Fail_ProfessorNaoEncontrado() {
        Long professorId = 999L; // ID que não existe
        TurmaRequest request = new TurmaRequest("Turma C", "TURMA789", "2024.3", professorId);

        when(professorRepository.findById(professorId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                turmaService.criarTurma(request)
        );

        assertEquals("Professor não encontrado", exception.getMessage());
        verify(professorRepository, times(1)).findById(professorId);
        verify(turmaRepository, never()).save(any());
    }
}
