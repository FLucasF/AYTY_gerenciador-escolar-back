package br.com.ufpb.GerenciadorEscolar.service.turma;

import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoResponse;
import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.model.Turma;
import jakarta.validation.constraints.Null;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TurmaServiceImplRemoverAlunoTest extends BaseTurmaServiceTest {

    // ✅ **Caso de sucesso**: Remover aluno corretamente
    @Test
    public void testRemoverAlunoDaTurma_Sucesso() {
        Long turmaId = 1L;
        Long alunoId = 2L;
        Turma turma = criarTurmaPadrao();
        Aluno aluno = criarAlunoPadrao();

        // Adiciona o aluno à turma antes da remoção
        turma.setAlunos(new ArrayList<>(List.of(aluno)));
        aluno.setTurmas(new ArrayList<>(List.of(turma)));

        when(turmaRepository.findById(turmaId)).thenReturn(Optional.of(turma));
        when(alunoRepository.findById(alunoId)).thenReturn(Optional.of(aluno));

        // Executa a remoção
        turmaService.removerAlunoDaTurma(turmaId, alunoId);

        // Verifica se o aluno foi removido corretamente
        assertFalse(turma.getAlunos().contains(aluno), "O aluno deveria ter sido removido da turma.");
        assertFalse(aluno.getTurmas().contains(turma), "A turma deveria ter sido removida do aluno.");

        // Verificações do mock (sem existsById, já que não é mais chamado no service)
        verify(turmaRepository, times(1)).findById(turmaId);
        verify(alunoRepository, times(1)).findById(alunoId);
        verify(turmaRepository, times(1)).save(turma);
    }



    // ❌ **Caso de falha**: Turma não encontrada
    @Test
    public void testRemoverAlunoDaTurma_TurmaNaoEncontrada() {
        Long turmaId = 99L;
        Long alunoId = 2L;

        when(turmaRepository.findById(turmaId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                turmaService.removerAlunoDaTurma(turmaId, alunoId)
        );

        assertEquals("Turma não encontrada", exception.getMessage());
        verify(turmaRepository, times(1)).findById(turmaId);
        verify(alunoRepository, never()).findById(any());
        verify(turmaRepository, never()).save(any());
    }

    // ❌ **Caso de falha**: Aluno não encontrado
    @Test
    public void testRemoverAlunoDaTurma_AlunoNaoEncontrado() {
        Long turmaId = 1L;
        Long alunoId = 99L;
        Turma turma = criarTurmaPadrao();

        when(turmaRepository.findById(turmaId)).thenReturn(Optional.of(turma));
        when(alunoRepository.findById(alunoId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                turmaService.removerAlunoDaTurma(turmaId, alunoId)
        );

        assertEquals("Aluno não encontrado", exception.getMessage());
        verify(turmaRepository, times(1)).findById(turmaId);
        verify(alunoRepository, times(1)).findById(alunoId);
        verify(turmaRepository, never()).save(any());
    }

    @Test
    public void testRemoverAlunoDaTurma_AlunoNaoMatriculado() {
        Long turmaId = 1L;
        Long alunoId = 2L;
        Turma turma = criarTurmaPadrao();
        turma.setAlunos(new ArrayList<>()); // ✅ Garante que a lista não será nula
        Aluno aluno = criarAlunoPadrao();

        when(turmaRepository.findById(turmaId)).thenReturn(Optional.of(turma));
        when(alunoRepository.findById(alunoId)).thenReturn(Optional.of(aluno));

        Exception exception = assertThrows(RuntimeException.class, () ->
                turmaService.removerAlunoDaTurma(turmaId, alunoId)
        );

        assertEquals("Aluno não está matriculado nesta turma.", exception.getMessage());
        verify(turmaRepository, times(1)).findById(turmaId);
        verify(alunoRepository, times(1)).findById(alunoId);
        verify(turmaRepository, never()).save(any());
    }


    // ❌ **Caso de falha**: ID da turma é nulo
    @Test
    public void testRemoverAlunoDaTurma_TurmaIdNulo() {
        Long alunoId = 2L;

        Exception exception = assertThrows(NullPointerException.class, () ->
                turmaService.removerAlunoDaTurma(null, alunoId)
        );

        assertEquals("ID não pode ser nulo", exception.getMessage());
        verify(turmaRepository, never()).findById(any());
        verify(alunoRepository, never()).findById(any());
        verify(turmaRepository, never()).save(any());
    }

    // ❌ **Caso de falha**: ID do aluno é nulo
    @Test
    public void testRemoverAlunoDaTurma_AlunoIdNulo() {
        Long turmaId = 1L;

        Exception exception = assertThrows(NullPointerException.class, () ->
                turmaService.removerAlunoDaTurma(turmaId, null)
        );

        assertEquals("ID não pode ser nulo", exception.getMessage());
        verify(turmaRepository, never()).findById(any());
        verify(alunoRepository, never()).findById(any());
        verify(turmaRepository, never()).save(any());
    }

    // ❌ **Caso de falha**: ID da turma é negativo
    @Test
    public void testRemoverAlunoDaTurma_TurmaIdNegativo() {
        Long alunoId = 2L;

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                turmaService.removerAlunoDaTurma(-1L, alunoId)
        );

        assertEquals("ID não pode ser negativo", exception.getMessage());
        verify(turmaRepository, never()).findById(any());
        verify(alunoRepository, never()).findById(any());
        verify(turmaRepository, never()).save(any());
    }

    // ❌ **Caso de falha**: ID do aluno é negativo
    @Test
    public void testRemoverAlunoDaTurma_AlunoIdNegativo() {
        Long turmaId = 1L;

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                turmaService.removerAlunoDaTurma(turmaId, -2L)
        );

        assertEquals("ID não pode ser negativo", exception.getMessage());
        verify(turmaRepository, never()).findById(any());
        verify(alunoRepository, never()).findById(any());
        verify(turmaRepository, never()).save(any());
    }
}
