package br.com.ufpb.GerenciadorEscolar.service.professor;

import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorRequest;
import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorResponse;
import br.com.ufpb.GerenciadorEscolar.model.Professor;
import br.com.ufpb.GerenciadorEscolar.model.UserLogin;
import br.com.ufpb.GerenciadorEscolar.service.ProfessorNaoEncontradoException;
import br.com.ufpb.GerenciadorEscolar.service.EmailJaCadastradoException;
import br.com.ufpb.GerenciadorEscolar.service.SiapeJaCadastradoException;
import br.com.ufpb.GerenciadorEscolar.service.NenhumaAlteracaoRealizadaException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfessorServiceImplAtualizarTest extends BaseProfessorServiceTest {

    @Test
    void deveAtualizarProfessorComSucesso() {
        // Arrange
        Professor professor = criarProfessorPadrao();
        UserLogin userLogin = criarUserLoginPadrao(professor);

        ProfessorRequest request = new ProfessorRequest(
                "Novo Nome", professor.getEmail(), null,
                professor.getCpf(), "Novo Departamento", professor.getSiape()
        );

        when(professorRepository.findByIdAndAtivoTrue(professor.getId())).thenReturn(Optional.of(professor));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(professor)).thenReturn(Optional.of(userLogin));
        when(professorMapper.toResponse(any())).thenReturn(mock(ProfessorResponse.class));

        // Act
        ProfessorResponse response = professorService.atualizarProfessor(professor.getId(), request);

        // Assert
        assertNotNull(response);
        assertEquals("Novo Nome", professor.getNome());
        assertEquals("Novo Departamento", professor.getDepartamento());

        verify(professorRepository).save(professor);
        verify(userLoginRepository, never()).save(any()); // Nenhuma alteração no UserLogin
    }

    @Test
    void deveLancarExcecao_SeSiapeJaEstiverCadastrado() {
        // Arrange
        Professor professor = criarProfessorPadrao();
        UserLogin userLogin = criarUserLoginPadrao(professor);

        ProfessorRequest request = new ProfessorRequest(
                professor.getNome(), professor.getEmail(), null,
                professor.getCpf(), professor.getDepartamento(), "SIAPE_DUPLICADO"
        );

        when(professorRepository.findByIdAndAtivoTrue(professor.getId())).thenReturn(Optional.of(professor));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(professor)).thenReturn(Optional.of(userLogin));
        when(professorRepository.findBySiapeAndAtivoTrue("SIAPE_DUPLICADO")).thenReturn(Optional.of(new Professor()));

        // Act & Assert
        assertThrows(SiapeJaCadastradoException.class,
                () -> professorService.atualizarProfessor(professor.getId(), request));

        verify(professorRepository).findByIdAndAtivoTrue(professor.getId());
        verify(professorRepository).findBySiapeAndAtivoTrue("SIAPE_DUPLICADO");
        verify(professorRepository, never()).save(any());
        verify(userLoginRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecao_SeNenhumaAlteracaoForFeita() {
        // Arrange
        Professor professor = criarProfessorPadrao();
        UserLogin userLogin = criarUserLoginPadrao(professor);
        ProfessorRequest request = criarProfessorRequest(
                professor.getNome(), professor.getEmail(), null,
                professor.getCpf(), professor.getDepartamento(), professor.getSiape()
        );

        when(professorRepository.findByIdAndAtivoTrue(professor.getId())).thenReturn(Optional.of(professor));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(professor)).thenReturn(Optional.of(userLogin));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true); // Simula senha igual

        // Act & Assert
        assertThrows(NenhumaAlteracaoRealizadaException.class,
                () -> professorService.atualizarProfessor(professor.getId(), request));

        verify(professorRepository, never()).save(any());
        verify(userLoginRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecao_SeProfessorNaoForEncontrado() {
        when(professorRepository.findByIdAndAtivoTrue(2L)).thenReturn(Optional.empty());

        assertThrows(ProfessorNaoEncontradoException.class,
                () -> professorService.atualizarProfessor(2L, criarProfessorRequest(
                        "Novo Nome", "novo@email.com", "NovaSenha@123",
                        "12345678900", "Computação", "1234567"
                )));
    }

    @Test
    void deveLancarExcecao_SeUserLoginNaoForEncontrado() {
        Professor professor = criarProfessorPadrao();

        when(professorRepository.findByIdAndAtivoTrue(professor.getId())).thenReturn(Optional.of(professor));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(professor)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> professorService.atualizarProfessor(professor.getId(), criarProfessorRequestPadrao()));
    }

    @Test
    void deveLancarExcecao_SeSenhaInformadaForIgual() {
        Professor professor = criarProfessorPadrao();
        UserLogin userLogin = criarUserLoginPadrao(professor);

        ProfessorRequest request = new ProfessorRequest(
                professor.getNome(), professor.getEmail(), "Senha@123",
                professor.getCpf(), professor.getDepartamento(), professor.getSiape()
        );

        when(professorRepository.findByIdAndAtivoTrue(professor.getId())).thenReturn(Optional.of(professor));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(professor)).thenReturn(Optional.of(userLogin));
        when(passwordEncoder.matches("Senha@123", professor.getSenha())).thenReturn(true);

        assertThrows(NenhumaAlteracaoRealizadaException.class,
                () -> professorService.atualizarProfessor(professor.getId(), request));

        verify(professorRepository, never()).save(any());
        verify(userLoginRepository, never()).save(any());
    }

    @Test
    void deveAtualizarSenha_SeInformadaDiferente() {
        Professor professor = criarProfessorPadrao();
        UserLogin userLogin = criarUserLoginPadrao(professor);

        ProfessorRequest request = new ProfessorRequest(
                professor.getNome(), professor.getEmail(), "NovaSenha@123",
                professor.getCpf(), professor.getDepartamento(), professor.getSiape()
        );

        when(professorRepository.findByIdAndAtivoTrue(professor.getId())).thenReturn(Optional.of(professor));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(professor)).thenReturn(Optional.of(userLogin));
        when(passwordEncoder.encode("NovaSenha@123")).thenReturn("NovaSenhaCriptografada");

        professorService.atualizarProfessor(professor.getId(), request);

        assertEquals("NovaSenhaCriptografada", professor.getSenha());
        assertEquals("NovaSenhaCriptografada", userLogin.getSenha());

        verify(professorRepository).save(professor);
        verify(userLoginRepository).save(userLogin);
    }

    @Test
    void deveAtualizarEmail_SeInformadoDiferente() {
        Professor professor = criarProfessorPadrao();
        UserLogin userLogin = criarUserLoginPadrao(professor);

        ProfessorRequest request = new ProfessorRequest(
                professor.getNome(), "novo@email.com", null,
                professor.getCpf(), professor.getDepartamento(), professor.getSiape()
        );

        when(professorRepository.findByIdAndAtivoTrue(professor.getId())).thenReturn(Optional.of(professor));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(professor)).thenReturn(Optional.of(userLogin));

        professorService.atualizarProfessor(professor.getId(), request);

        assertEquals("novo@email.com", professor.getEmail());
        assertEquals("novo@email.com", userLogin.getEmail());

        verify(professorRepository).save(professor);
        verify(userLoginRepository).save(userLogin);
    }
}
