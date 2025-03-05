package br.com.ufpb.GerenciadorEscolar.service.aluno;

import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.model.UserLogin;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AlunoServiceImplDesativarTest extends BaseAlunoServiceTest {

    // ✅ Cenário 1: Aluno desativado com sucesso
    @Test
    public void testDesativarAluno_Success() {
        Aluno aluno = criarAlunoAtivo();
        UserLogin userLogin = criarUserLoginAtivo();

        when(alunoRepository.findByIdAndAtivoTrue(aluno.getId())).thenReturn(Optional.of(aluno));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(aluno)).thenReturn(Optional.of(userLogin));

        alunoService.desativarAluno(aluno.getId());

        assertFalse(aluno.isAtivo());
        assertFalse(userLogin.isAtivo());

        verify(alunoRepository, times(1)).save(aluno);
        verify(userLoginRepository, times(1)).save(userLogin);
    }

    // ✅ Cenário 2: Aluno não encontrado
    @Test
    public void testDesativarAluno_NotFound() {
        when(alunoRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                alunoService.desativarAluno(1L)
        );

        assertEquals("Aluno não encontrado", exception.getMessage());

        verify(alunoRepository, never()).save(any());
        verify(userLoginRepository, never()).save(any());
    }

    // ✅ Cenário 3: Aluno já está inativo
    @Test
    public void testDesativarAluno_AlreadyInactive() {
        Aluno aluno = criarAlunoInativo();

        when(alunoRepository.findByIdAndAtivoTrue(aluno.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                alunoService.desativarAluno(aluno.getId())
        );

        assertEquals("Aluno não encontrado", exception.getMessage());

        verify(alunoRepository, never()).save(any());
        verify(userLoginRepository, never()).save(any());
    }

    // ✅ Cenário 4: UserLogin não encontrado (desativa apenas o aluno)
    @Test
    public void testDesativarAluno_UserLoginNotFound() {
        Aluno aluno = criarAlunoAtivo();

        when(alunoRepository.findByIdAndAtivoTrue(aluno.getId())).thenReturn(Optional.of(aluno));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(aluno)).thenReturn(Optional.empty());

        alunoService.desativarAluno(aluno.getId());

        assertFalse(aluno.isAtivo());

        verify(alunoRepository, times(1)).save(aluno);
        verify(userLoginRepository, never()).save(any());
    }

    // ✅ Cenário 5: UserLogin já estava inativo (não deve chamar `save`)
    @Test
    public void testDesativarAluno_UserLoginAlreadyInactive() {
        Aluno aluno = criarAlunoAtivo();
        UserLogin userLogin = criarUserLoginInativo();

        when(alunoRepository.findByIdAndAtivoTrue(aluno.getId())).thenReturn(Optional.of(aluno));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(aluno)).thenReturn(Optional.of(userLogin));

        alunoService.desativarAluno(aluno.getId());

        assertFalse(aluno.isAtivo());

        verify(alunoRepository, times(1)).save(aluno);
        verify(userLoginRepository, never()).save(userLogin);
    }

    // ✅ Cenário 6: Erro ao salvar o aluno no banco
    @Test
    public void testDesativarAluno_ErrorSavingAluno() {
        Aluno aluno = criarAlunoAtivo();
        UserLogin userLogin = criarUserLoginAtivo();

        when(alunoRepository.findByIdAndAtivoTrue(aluno.getId())).thenReturn(Optional.of(aluno));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(aluno)).thenReturn(Optional.of(userLogin));

        doThrow(new RuntimeException("Erro ao salvar aluno")).when(alunoRepository).save(any());

        Exception exception = assertThrows(RuntimeException.class, () ->
                alunoService.desativarAluno(aluno.getId())
        );

        assertEquals("Erro ao salvar aluno", exception.getMessage());

        verify(userLoginRepository, times(1)).save(userLogin);
        verify(alunoRepository, times(1)).save(aluno);
    }
}
