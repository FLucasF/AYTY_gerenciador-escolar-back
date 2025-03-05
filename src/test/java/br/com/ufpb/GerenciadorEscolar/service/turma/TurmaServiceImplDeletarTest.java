package br.com.ufpb.GerenciadorEscolar.service.turma;

import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.model.Turma;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TurmaServiceImplDeletarTest extends BaseTurmaServiceTest {

    // ✅ Teste de sucesso: Turma sem alunos deve ser deletada corretamente
    @Test
    public void testDeletarTurma_Sucesso() {
        Long turmaId = 1L;
        Turma turma = criarTurmaPadrao();
        turma.setAlunos(new ArrayList<>()); // Nenhum aluno matriculado

        when(turmaRepository.findById(turmaId)).thenReturn(Optional.of(turma));

        assertDoesNotThrow(() -> turmaService.deletarTurma(turmaId));

        verify(turmaRepository, times(1)).findById(turmaId);
        verify(turmaRepository, times(1)).deleteById(turmaId);
    }

    // ❌ Falha: Tentativa de deletar uma turma que contém alunos
    @Test
    public void testDeletarTurma_ComAlunos_Falha() {
        Long turmaId = 1L;
        Turma turma = criarTurmaPadrao();

        // Criamos alunos fictícios para simular uma turma com alunos
        List<Aluno> alunos = new ArrayList<>();
        Aluno aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("Aluno Teste");
        alunos.add(aluno);

        turma.setAlunos(alunos); // Adicionando aluno à turma

        when(turmaRepository.findById(turmaId)).thenReturn(Optional.of(turma));

        Exception exception = assertThrows(RuntimeException.class, () -> turmaService.deletarTurma(turmaId));

        assertEquals("A turma ainda tem alunos matriculados e não pode ser deletada.", exception.getMessage());

        verify(turmaRepository, times(1)).findById(turmaId);
        verify(turmaRepository, never()).deleteById(any());
    }

    // ❌ Falha: Tentativa de deletar uma turma que não existe
    @Test
    public void testDeletarTurma_NaoEncontrada() {
        Long turmaId = 99L;

        when(turmaRepository.findById(turmaId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> turmaService.deletarTurma(turmaId));

        assertEquals("Turma não encontrada", exception.getMessage());

        verify(turmaRepository, times(1)).findById(turmaId);
        verify(turmaRepository, never()).deleteById(any());
    }

    // ❌ Falha: Tentativa de deletar turma com ID nulo
    @Test
    public void testDeletarTurma_IdNulo() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> turmaService.deletarTurma(null));

        assertEquals("ID da turma não pode ser nulo", exception.getMessage());

        verify(turmaRepository, never()).findById(any());
        verify(turmaRepository, never()).deleteById(any());
    }

    // ❌ Falha: Tentativa de deletar turma com ID negativo
    @Test
    public void testDeletarTurma_IdNegativo() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> turmaService.deletarTurma(-1L));

        assertEquals("ID da turma não pode ser negativo", exception.getMessage());

        verify(turmaRepository, never()).findById(any());
        verify(turmaRepository, never()).deleteById(any());
    }

    // ❌ Falha: Exceção inesperada do banco de dados ao tentar deletar a turma
    @Test
    public void testDeletarTurma_ErroNoBanco() {
        Long turmaId = 1L;
        Turma turma = criarTurmaPadrao();
        turma.setAlunos(new ArrayList<>()); // Sem alunos

        when(turmaRepository.findById(turmaId)).thenReturn(Optional.of(turma));
        doThrow(new RuntimeException("Erro ao deletar no banco")).when(turmaRepository).deleteById(turmaId);

        Exception exception = assertThrows(RuntimeException.class, () -> turmaService.deletarTurma(turmaId));

        assertEquals("Erro ao deletar no banco", exception.getMessage());

        verify(turmaRepository, times(1)).findById(turmaId);
        verify(turmaRepository, times(1)).deleteById(turmaId);
    }
}
