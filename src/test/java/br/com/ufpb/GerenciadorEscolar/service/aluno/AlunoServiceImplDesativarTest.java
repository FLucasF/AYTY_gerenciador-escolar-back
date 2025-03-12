package br.com.ufpb.GerenciadorEscolar.service.aluno;

import br.com.ufpb.GerenciadorEscolar.model.entity.Aluno;
import br.com.ufpb.GerenciadorEscolar.model.entity.UserLogin;
import br.com.ufpb.GerenciadorEscolar.service.AlunoNaoEncontradoException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlunoServiceImplDesativarTest extends BaseAlunoServiceTest {

    @Test
    void deveDesativarAlunoComSucesso() {
        // Arrange
        Aluno aluno = criarAlunoPadrao();
        aluno.setAtivo(true);
        UserLogin userLogin = criarUserLoginPadrao(aluno);
        userLogin.setAtivo(true);

        when(alunoRepository.findByIdAndAtivoTrue(aluno.getId())).thenReturn(Optional.of(aluno));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(aluno)).thenReturn(Optional.of(userLogin));

        // Act
        alunoService.desativarAluno(aluno.getId());

        // Assert
        assertFalse(aluno.isAtivo());
        assertFalse(userLogin.isAtivo());

        verify(alunoRepository).save(aluno);
        verify(userLoginRepository).save(userLogin);
    }

    @Test
    void deveLancarExcecao_SeAlunoNaoForEncontrado() {
        // Arrange
        Long idInexistente = 99L;
        when(alunoRepository.findByIdAndAtivoTrue(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AlunoNaoEncontradoException.class, () -> alunoService.desativarAluno(idInexistente));

        verify(alunoRepository, never()).save(any());
        verify(userLoginRepository, never()).save(any());
    }

    @Test
    void deveDesativarAlunoMesmoSemLogin() {
        // Arrange
        Aluno aluno = criarAlunoPadrao();
        aluno.setAtivo(true);

        when(alunoRepository.findByIdAndAtivoTrue(aluno.getId())).thenReturn(Optional.of(aluno));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(aluno)).thenReturn(Optional.empty());

        // Act
        alunoService.desativarAluno(aluno.getId());

        // Assert
        assertFalse(aluno.isAtivo());
        verify(alunoRepository).save(aluno);
        verify(userLoginRepository, never()).save(any());
    }

    @Test
    void deveManterStatusSeAlunoJaEstiverInativo() {
        // Arrange
        Aluno aluno = criarAlunoPadrao();
        aluno.setAtivo(false);
        UserLogin userLogin = criarUserLoginPadrao(aluno);
        userLogin.setAtivo(false);

        when(alunoRepository.findByIdAndAtivoTrue(aluno.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AlunoNaoEncontradoException.class, () -> alunoService.desativarAluno(aluno.getId()));

        verify(alunoRepository, never()).save(any());
        verify(userLoginRepository, never()).save(any());
    }
}
