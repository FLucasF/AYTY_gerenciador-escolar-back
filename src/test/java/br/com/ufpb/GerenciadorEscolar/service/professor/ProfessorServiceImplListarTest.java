package br.com.ufpb.GerenciadorEscolar.service.professor;

import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorResponse;
import br.com.ufpb.GerenciadorEscolar.model.Professor;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProfessorServiceImplListarTest extends BaseProfessorServiceTest {

    // ✅ Testa listagem com professores ativos
    @Test
    public void testListarProfessoresAtivos_Success() {
        PageRequest pageable = PageRequest.of(0, 10);
        Professor professor = new Professor();
        professor.setId(1L);

        ProfessorResponse response = new ProfessorResponse(
                professor.getId(), "Nome do Professor", "email@teste.com", "12345678901", "Departamento", "1234567"
        );
        when(professorMapper.toResponse(professor)).thenReturn(response);

        Page<Professor> professorPage = new PageImpl<>(Collections.singletonList(professor));
        when(professorRepository.findAllByAtivoTrue(pageable)).thenReturn(professorPage);

        Page<ProfessorResponse> result = professorService.listarProfessoresAtivos(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(response, result.getContent().get(0));
        verify(professorRepository, times(1)).findAllByAtivoTrue(pageable);
    }

    // ✅ Testa listagem quando não há professores ativos
    @Test
    public void testListarProfessoresAtivos_EmptyList() {
        PageRequest pageable = PageRequest.of(0, 10);

        Page<Professor> emptyPage = new PageImpl<>(Collections.emptyList());
        when(professorRepository.findAllByAtivoTrue(pageable)).thenReturn(emptyPage);

        Page<ProfessorResponse> result = professorService.listarProfessoresAtivos(pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
        verify(professorRepository, times(1)).findAllByAtivoTrue(pageable);
    }

    // ✅ Testa erro no banco de dados
    @Test
    public void testListarProfessoresAtivos_DatabaseFailure() {
        PageRequest pageable = PageRequest.of(0, 10);

        when(professorRepository.findAllByAtivoTrue(pageable))
                .thenThrow(new RuntimeException("Erro ao acessar banco de dados"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                professorService.listarProfessoresAtivos(pageable)
        );

        assertEquals("Erro ao acessar banco de dados", exception.getMessage());
        verify(professorRepository, times(1)).findAllByAtivoTrue(pageable);
    }

    // ❌ **Teste: Falha ao converter entidade para DTO** → Deve falhar
    @Test
    public void testListarProfessoresAtivos_FailureOnMapping() {
        PageRequest pageable = PageRequest.of(0, 10);
        Professor professor = new Professor();
        professor.setId(1L);

        Page<Professor> professorPage = new PageImpl<>(Collections.singletonList(professor));
        when(professorRepository.findAllByAtivoTrue(pageable)).thenReturn(professorPage);

        // Simula falha ao converter entidade para DTO
        when(professorMapper.toResponse(professor))
                .thenThrow(new RuntimeException("Erro ao converter professor para DTO"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                professorService.listarProfessoresAtivos(pageable)
        );

        assertEquals("Erro ao converter professor para DTO", exception.getMessage());
    }
}
