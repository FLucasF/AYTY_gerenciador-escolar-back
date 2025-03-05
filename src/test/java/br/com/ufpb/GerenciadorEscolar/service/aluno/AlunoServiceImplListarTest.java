package br.com.ufpb.GerenciadorEscolar.service.aluno;

import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoResponse;
import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AlunoServiceImplListarTest extends BaseAlunoServiceTest {

    // ✅ Testa listagem com alunos ativos
    @Test
    public void testListarAlunosAtivos_Success() {
        PageRequest pageable = PageRequest.of(0, 10);
        Aluno aluno = criarAlunoAtivo();
        AlunoResponse response = criarAlunoResponse(aluno);

        when(alunoMapper.toResponse(aluno)).thenReturn(response);

        Page<Aluno> alunoPage = new PageImpl<>(Collections.singletonList(aluno));
        when(alunoRepository.findAllByAtivoTrue(pageable)).thenReturn(alunoPage);

        Page<AlunoResponse> result = alunoService.listarAlunosAtivos(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(response, result.getContent().get(0));
        verify(alunoRepository, times(1)).findAllByAtivoTrue(pageable);
    }

    // ✅ Testa listagem quando não há alunos ativos
    @Test
    public void testListarAlunosAtivos_EmptyList() {
        PageRequest pageable = PageRequest.of(0, 10);

        Page<Aluno> emptyPage = new PageImpl<>(Collections.emptyList());
        when(alunoRepository.findAllByAtivoTrue(pageable)).thenReturn(emptyPage);

        Page<AlunoResponse> result = alunoService.listarAlunosAtivos(pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
        verify(alunoRepository, times(1)).findAllByAtivoTrue(pageable);
    }

    // ✅ Testa erro no banco de dados
    @Test
    public void testListarAlunosAtivos_DatabaseFailure() {
        PageRequest pageable = PageRequest.of(0, 10);

        when(alunoRepository.findAllByAtivoTrue(pageable))
                .thenThrow(new RuntimeException("Erro ao acessar banco de dados"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                alunoService.listarAlunosAtivos(pageable)
        );

        assertEquals("Erro ao acessar banco de dados", exception.getMessage());
        verify(alunoRepository, times(1)).findAllByAtivoTrue(pageable);
    }

    // ✅ Testa falha ao converter entidade para DTO
    @Test
    public void testListarAlunosAtivos_FailureOnMapping() {
        PageRequest pageable = PageRequest.of(0, 10);
        Aluno aluno = criarAlunoAtivo();

        Page<Aluno> alunoPage = new PageImpl<>(Collections.singletonList(aluno));
        when(alunoRepository.findAllByAtivoTrue(pageable)).thenReturn(alunoPage);

        // Simula falha ao converter entidade para DTO
        when(alunoMapper.toResponse(aluno))
                .thenThrow(new RuntimeException("Erro ao converter aluno para DTO"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                alunoService.listarAlunosAtivos(pageable)
        );

        assertEquals("Erro ao converter aluno para DTO", exception.getMessage());
    }
}
