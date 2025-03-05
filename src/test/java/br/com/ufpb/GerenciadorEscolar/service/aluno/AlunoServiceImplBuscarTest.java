package br.com.ufpb.GerenciadorEscolar.service.aluno;

import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoResponse;
import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.repository.AlunoRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
public class AlunoServiceImplBuscarTest extends BaseAlunoServiceTest {

    @Test
    public void testBuscarAlunoPorId_Success() {
        Aluno aluno = criarAlunoPadrao();
        AlunoResponse response = criarAlunoResponse(aluno);

        when(alunoRepository.findByIdAndAtivoTrue(aluno.getId())).thenReturn(Optional.of(aluno));
        when(alunoMapper.toResponse(aluno)).thenReturn(response);

        Optional<AlunoResponse> result = alunoService.buscarAlunoPorId(aluno.getId());

        assertTrue(result.isPresent());
        assertEquals(response, result.get());
        verify(alunoRepository, times(1)).findByIdAndAtivoTrue(aluno.getId());
    }

    @Test
    public void testBuscarAlunoPorId_NotFound() {
        Long id = 1L;

        when(alunoRepository.findByIdAndAtivoTrue(id)).thenReturn(Optional.empty());

        Optional<AlunoResponse> result = alunoService.buscarAlunoPorId(id);

        assertFalse(result.isPresent(), "Aluno encontrado quando não deveria.");
        verify(alunoRepository, times(1)).findByIdAndAtivoTrue(id);
    }

    @Test
    public void testBuscarAlunoPorId_InvalidId() {
        Long invalidId = -1L;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            alunoService.buscarAlunoPorId(invalidId);
        });

        assertEquals("ID não pode ser nulo ou inválido", exception.getMessage());
    }

}
