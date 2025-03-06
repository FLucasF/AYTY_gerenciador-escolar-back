package br.com.ufpb.GerenciadorEscolar.service.professor;

import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorRequest;
import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorResponse;
import br.com.ufpb.GerenciadorEscolar.model.Professor;
import br.com.ufpb.GerenciadorEscolar.model.UserLogin;
import br.com.ufpb.GerenciadorEscolar.service.CpfJaCadastradoException;
import br.com.ufpb.GerenciadorEscolar.service.EmailJaCadastradoException;
import br.com.ufpb.GerenciadorEscolar.service.SiapeJaCadastradoException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfessorServiceImplCadastrarTest extends BaseProfessorServiceTest {

    @Test
    void deveCadastrarProfessorELoginComSucesso() {
        // Arrange
        ProfessorRequest request = criarProfessorRequest(
                "Lucas Silva", "lucas@email.com", "Senha@123",
                "98765432100", "Matemática", "1234567"
        );

        Professor professor = criarProfessorPadrao();

        when(professorRepository.findByEmailAndAtivoTrue(request.email())).thenReturn(Optional.empty());
        when(professorRepository.findByCpfAndAtivoTrue(request.cpf())).thenReturn(Optional.empty());
        when(professorRepository.findBySiapeAndAtivoTrue(request.siape())).thenReturn(Optional.empty());
        when(professorMapper.toEntity(request)).thenReturn(professor);
        when(passwordEncoder.encode(request.senha())).thenReturn("SenhaCriptografada");
        when(professorMapper.toResponse(professor)).thenReturn(mock(ProfessorResponse.class));

        // Act
        ProfessorResponse response = professorService.cadastrarProfessor(request);

        // Assert
        assertNotNull(response);

        // 🔍 Capturando os objetos salvos
        ArgumentCaptor<Professor> professorCaptor = ArgumentCaptor.forClass(Professor.class);
        ArgumentCaptor<UserLogin> userLoginCaptor = ArgumentCaptor.forClass(UserLogin.class);

        verify(professorRepository).save(professorCaptor.capture());
        verify(userLoginRepository).save(userLoginCaptor.capture());

        Professor professorSalvo = professorCaptor.getValue();
        UserLogin userLoginSalvo = userLoginCaptor.getValue();

        // ✅ Verificações no Professor salvo
        assertEquals("SenhaCriptografada", professorSalvo.getSenha(), "A senha do professor deve estar criptografada.");
        assertEquals(request.nome(), professorSalvo.getNome());
        assertEquals(request.email(), professorSalvo.getEmail());
        assertEquals(request.cpf(), professorSalvo.getCpf());
        assertEquals(request.departamento(), professorSalvo.getDepartamento());
        assertEquals(request.siape(), professorSalvo.getSiape());

        // ✅ Verificações no UserLogin salvo
        assertEquals(professorSalvo.getEmail(), userLoginSalvo.getEmail(), "O email do login deve ser igual ao do professor.");
        assertEquals("SenhaCriptografada", userLoginSalvo.getSenha(), "A senha criptografada deve ser a mesma no UserLogin.");
        assertEquals(professorSalvo, userLoginSalvo.getUsuario(), "O login deve estar associado ao professor criado.");
    }

    @Test
    void deveLancarExcecao_SeEmailJaCadastrado() {
        // Arrange
        ProfessorRequest request = criarProfessorRequest(
                "Lucas Silva", "lucas@email.com", "Senha@123",
                "98765432100", "Matemática", "1234567"
        );

        when(professorRepository.findByEmailAndAtivoTrue(request.email())).thenReturn(Optional.of(criarProfessorPadrao()));

        // Act & Assert
        assertThrows(EmailJaCadastradoException.class,
                () -> professorService.cadastrarProfessor(request));

        verify(professorRepository, never()).save(any());
        verify(userLoginRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecao_SeCpfJaCadastrado() {
        // Arrange
        ProfessorRequest request = criarProfessorRequest(
                "Lucas Silva", "lucas@email.com", "Senha@123",
                "98765432100", "Matemática", "1234567"
        );

        when(professorRepository.findByEmailAndAtivoTrue(request.email())).thenReturn(Optional.empty());
        when(professorRepository.findByCpfAndAtivoTrue(request.cpf())).thenReturn(Optional.of(criarProfessorPadrao()));

        // Act & Assert
        assertThrows(CpfJaCadastradoException.class,
                () -> professorService.cadastrarProfessor(request));

        verify(professorRepository, never()).save(any());
        verify(userLoginRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecao_SeSiapeJaCadastrado() {
        // Arrange
        ProfessorRequest request = criarProfessorRequest(
                "Lucas Silva", "lucas@email.com", "Senha@123",
                "98765432100", "Matemática", "1234567"
        );

        when(professorRepository.findBySiapeAndAtivoTrue(request.siape())).thenReturn(Optional.of(criarProfessorPadrao()));

        // Act & Assert
        assertThrows(SiapeJaCadastradoException.class,
                () -> professorService.cadastrarProfessor(request));

        verify(professorRepository, never()).save(any());
        verify(userLoginRepository, never()).save(any());
    }
}
