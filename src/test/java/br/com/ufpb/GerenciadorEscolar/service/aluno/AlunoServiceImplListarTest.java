package br.com.ufpb.GerenciadorEscolar.service.aluno;

import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoResponse;
import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlunoServiceImplListarTest extends BaseAlunoServiceTest {

    @Test
    void deveListarAlunosAtivosComSucesso() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Aluno aluno1 = criarAlunoPadrao();
        Aluno aluno2 = new Aluno();
        aluno2.setId(2L);
        aluno2.setNome("Maria Souza");
        aluno2.setEmail("maria@email.com");
        aluno2.setCpf("98765432100");
        aluno2.setCurso("Matemática");
        aluno2.setSenha("Senha@123");
        Page<Aluno> alunosPage = new PageImpl<>(List.of(aluno1, aluno2), pageable, 2);

        when(alunoRepository.findAllByAtivoTrue(pageable)).thenReturn(alunosPage);
        when(alunoMapper.toResponse(any())).thenAnswer(invocation -> {
            Aluno a = invocation.getArgument(0);
            return new AlunoResponse(a.getId(), a.getNome(), a.getEmail(), a.getCpf(), a.getCurso());
        });

        // Act
        Page<AlunoResponse> responsePage = alunoService.listarAlunosAtivos(pageable);

        // Assert
        assertNotNull(responsePage);
        assertEquals(2, responsePage.getTotalElements());
        assertEquals("Lucas Felipe", responsePage.getContent().get(0).nome());
        assertEquals("Maria Souza", responsePage.getContent().get(1).nome());

        verify(alunoRepository).findAllByAtivoTrue(pageable);
        verify(alunoMapper, times(2)).toResponse(any());
    }

    @Test
    void deveRetornarPaginaVazia_SeNaoHouverAlunosAtivos() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Aluno> alunosPage = Page.empty(pageable);

        when(alunoRepository.findAllByAtivoTrue(pageable)).thenReturn(alunosPage);

        // Act
        Page<AlunoResponse> responsePage = alunoService.listarAlunosAtivos(pageable);

        // Assert
        assertNotNull(responsePage);
        assertTrue(responsePage.isEmpty());

        verify(alunoRepository).findAllByAtivoTrue(pageable);
        verify(alunoMapper, never()).toResponse(any());
    }
}
