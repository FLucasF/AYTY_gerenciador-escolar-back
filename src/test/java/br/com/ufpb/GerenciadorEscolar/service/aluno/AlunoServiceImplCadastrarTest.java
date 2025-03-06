package br.com.ufpb.GerenciadorEscolar.service.aluno;

import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoRequest;
import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoResponse;
import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.model.UserLogin;
import br.com.ufpb.GerenciadorEscolar.service.CpfJaCadastradoException;
import br.com.ufpb.GerenciadorEscolar.service.EmailJaCadastradoException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlunoServiceImplCadastrarTest extends BaseAlunoServiceTest {

    @Test
    void deveCadastrarAlunoComSucesso() {
        // Arrange
        AlunoRequest request = criarAlunoRequest("Lucas Felipe", "lucas@email.com", "Senha@123", "12345678901", "Engenharia");

        Aluno aluno = criarAlunoPadrao();
        AlunoResponse alunoResponse = criarAlunoResponse(aluno);

        when(alunoRepository.findByEmailAndAtivoTrue(request.email())).thenReturn(Optional.empty());
        when(alunoRepository.findByCpfAndAtivoTrue(request.cpf())).thenReturn(Optional.empty());
        when(alunoMapper.toEntity(request)).thenReturn(aluno);
        when(passwordEncoder.encode(request.senha())).thenReturn("SenhaCriptografada");
        when(alunoMapper.toResponse(aluno)).thenReturn(alunoResponse);

        // Act
        AlunoResponse response = alunoService.cadastrarAluno(request);

        // Assert
        assertNotNull(response);
        assertEquals(alunoResponse, response);

        verify(alunoRepository).save(aluno);
        verify(userLoginRepository).save(any(UserLogin.class));
    }

    @Test
    void deveLancarExcecao_SeEmailJaEstiverCadastrado() {
        // Arrange
        AlunoRequest request = criarAlunoRequest("Lucas Felipe", "lucas@email.com", "Senha@123", "12345678901", "Engenharia");
        when(alunoRepository.findByEmailAndAtivoTrue(request.email())).thenReturn(Optional.of(new Aluno()));

        // Act & Assert
        assertThrows(EmailJaCadastradoException.class, () -> alunoService.cadastrarAluno(request));

        verify(alunoRepository, never()).save(any());
        verify(userLoginRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecao_SeCpfJaEstiverCadastrado() {
        // Arrange
        AlunoRequest request = criarAlunoRequest("Lucas Felipe", "lucas@email.com", "Senha@123", "12345678901", "Engenharia");
        when(alunoRepository.findByEmailAndAtivoTrue(request.email())).thenReturn(Optional.empty());
        when(alunoRepository.findByCpfAndAtivoTrue(request.cpf())).thenReturn(Optional.of(new Aluno()));

        // Act & Assert
        assertThrows(CpfJaCadastradoException.class, () -> alunoService.cadastrarAluno(request));

        verify(alunoRepository, never()).save(any());
        verify(userLoginRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecao_SeSenhaNaoForInformada() {
        // Arrange
        AlunoRequest request = criarAlunoRequest("Lucas Felipe", "lucas@email.com", null, "12345678901", "Engenharia");

        // Act & Assert
        assertThrows(NullPointerException.class, () -> alunoService.cadastrarAluno(request));

        verify(alunoRepository, never()).save(any());
        verify(userLoginRepository, never()).save(any());
    }
}
