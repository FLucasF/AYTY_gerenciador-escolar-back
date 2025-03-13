package br.com.ufpb.GerenciadorEscolar.service.turma;

import br.com.ufpb.GerenciadorEscolar.model.dto.turma.TurmaResponse;
import br.com.ufpb.GerenciadorEscolar.model.entity.Aluno;
import br.com.ufpb.GerenciadorEscolar.model.entity.Turma;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TurmaServiceImplMatricularAlunoTest extends BaseTurmaServiceTest {

    @Test
    public void testMatricularAluno_Sucesso() {
        Long turmaId = 1L;
        Long alunoId = 2L;

        Turma turma = criarTurmaPadrao();
        turma.setAlunos(new ArrayList<>()); // ✅ Inicializando a lista de alunos

        Aluno aluno = criarAlunoPadrao();
        aluno.setId(alunoId);
        aluno.setTurmas(new ArrayList<>()); // ✅ Inicializando a lista de turmas do aluno

        when(turmaRepository.findById(turmaId)).thenReturn(Optional.of(turma));
        when(alunoRepository.findById(alunoId)).thenReturn(Optional.of(aluno));
        when(turmaMapper.toResponse(any(Turma.class))).thenReturn(criarTurmaResponse(turma));

        TurmaResponse resultado = turmaService.matricularAluno(turmaId, alunoId);

        assertNotNull(resultado);
        assertTrue(turma.getAlunos().contains(aluno), "O aluno deveria estar matriculado na turma.");
        assertTrue(aluno.getTurmas().contains(turma), "A turma deveria estar na lista de turmas do aluno.");

        verify(turmaRepository, times(1)).findById(turmaId);
        verify(alunoRepository, times(1)).findById(alunoId);
        verify(turmaRepository, times(1)).save(turma);
    }



    @Test
    public void testMatricularAluno_AlunoJaMatriculado() {
        Long turmaId = 1L;
        Long alunoId = 2L;
        Turma turma = criarTurmaPadrao();
        Aluno aluno = criarAlunoPadrao();
        turma.setAlunos(List.of(aluno));

        when(turmaRepository.findById(turmaId)).thenReturn(Optional.of(turma));
        when(alunoRepository.findById(alunoId)).thenReturn(Optional.of(aluno));
        when(turmaMapper.toResponse(turma)).thenReturn(criarTurmaResponse(turma));

        TurmaResponse resultado = turmaService.matricularAluno(turmaId, alunoId);

        assertNotNull(resultado);
        verify(turmaRepository, never()).save(turma); // ✅ Não deve salvar pois já está matriculado
    }

    @Test
    public void testMatricularAluno_TurmaNaoEncontrada() {
        Long turmaId = 1L;
        Long alunoId = 2L;

        when(turmaRepository.findById(turmaId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> turmaService.matricularAluno(turmaId, alunoId));

        assertEquals("Turma não encontrada", exception.getMessage());
        verify(alunoRepository, never()).findById(any());
    }

    @Test
    public void testMatricularAluno_AlunoNaoEncontrado() {
        Long turmaId = 1L;
        Long alunoId = 2L;
        Turma turma = criarTurmaPadrao();

        when(turmaRepository.findById(turmaId)).thenReturn(Optional.of(turma));
        when(alunoRepository.findById(alunoId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> turmaService.matricularAluno(turmaId, alunoId));

        assertEquals("Aluno não encontrado", exception.getMessage());
        verify(turmaRepository, times(1)).findById(turmaId);
    }

    @Test
    public void testMatricularAluno_TurmaLotada() {
        Long turmaId = 1L;
        Long alunoId = 2L;
        Turma turma = criarTurmaPadrao();
        Aluno aluno = criarAlunoPadrao();
        turma.setAlunos(Collections.nCopies(turma.getTamanhoMaximo(), new Aluno())); // Turma cheia

        when(turmaRepository.findById(turmaId)).thenReturn(Optional.of(turma));
        when(alunoRepository.findById(alunoId)).thenReturn(Optional.of(aluno));

        Exception exception = assertThrows(RuntimeException.class, () -> turmaService.matricularAluno(turmaId, alunoId));

        assertEquals("A turma já atingiu o tamanho máximo de alunos.", exception.getMessage());
    }

    @Test
    public void testMatricularAluno_ErroBancoAoBuscarTurma() {
        Long turmaId = 1L;
        Long alunoId = 2L;

        when(turmaRepository.findById(turmaId)).thenThrow(new RuntimeException("Erro ao acessar banco"));

        Exception exception = assertThrows(RuntimeException.class, () -> turmaService.matricularAluno(turmaId, alunoId));

        assertEquals("Erro ao acessar banco", exception.getMessage());
    }

    @Test
    public void testMatricularAluno_ErroBancoAoBuscarAluno() {
        Long turmaId = 1L;
        Long alunoId = 2L;
        Turma turma = criarTurmaPadrao();

        when(turmaRepository.findById(turmaId)).thenReturn(Optional.of(turma));
        when(alunoRepository.findById(alunoId)).thenThrow(new RuntimeException("Erro ao acessar banco"));

        Exception exception = assertThrows(RuntimeException.class, () -> turmaService.matricularAluno(turmaId, alunoId));

        assertEquals("Erro ao acessar banco", exception.getMessage());
    }

    @Test
    public void testMatricularAluno_ErroAoSalvarMatricula() {
        Long turmaId = 1L;
        Long alunoId = 2L;
        Turma turma = criarTurmaPadrao();
        Aluno aluno = criarAlunoPadrao();

        when(turmaRepository.findById(turmaId)).thenReturn(Optional.of(turma));
        when(alunoRepository.findById(alunoId)).thenReturn(Optional.of(aluno));
        doThrow(new RuntimeException("Erro ao salvar")).when(turmaRepository).save(any());

        Exception exception = assertThrows(RuntimeException.class, () -> turmaService.matricularAluno(turmaId, alunoId));

        assertEquals("Erro ao salvar", exception.getMessage());
    }
}
