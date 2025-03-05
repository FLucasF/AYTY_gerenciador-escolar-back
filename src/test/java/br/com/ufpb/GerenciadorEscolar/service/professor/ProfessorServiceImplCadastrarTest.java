package br.com.ufpb.GerenciadorEscolar.service.professor;

import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorRequest;
import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorResponse;
import br.com.ufpb.GerenciadorEscolar.model.Professor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProfessorServiceImplCadastrarTest extends BaseProfessorServiceTest {

    @Test
    public void testCadastrarProfessor_Success() {
        ProfessorRequest request = criarProfessorRequest("Novo Professor", "novoemail@teste.com", "novaSenha", "12345678901", "Departamento A", "1234567");
        Professor professor = criarProfessorPadrao();
        ProfessorResponse response = criarProfessorResponse(professor);

        when(professorRepository.findByEmailAndAtivoTrue(request.email())).thenReturn(Optional.empty());
        when(professorRepository.findByCpfAndAtivoTrue(request.cpf())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.senha())).thenReturn("senhaCodificada");
        when(professorMapper.toEntity(request)).thenReturn(professor);
        when(professorRepository.save(any())).thenReturn(professor);
        when(professorMapper.toResponse(any())).thenReturn(response);

        ProfessorResponse resultado = professorService.cadastrarProfessor(request);

        assertNotNull(resultado);
        assertEquals(response, resultado);
        verify(professorRepository, times(1)).save(any());
    }

    @Test
    public void testCadastrarProfessor_DuplicateEmail() {
        ProfessorRequest request = criarProfessorRequest("Novo Professor", "emailduplicado@teste.com", "novaSenha", "12345678901", "Departamento A", "1234567");
        when(professorRepository.findByEmailAndAtivoTrue(request.email())).thenReturn(Optional.of(new Professor()));

        Exception exception = assertThrows(RuntimeException.class, () ->
                professorService.cadastrarProfessor(request)
        );

        assertEquals("Já existe um professor ativo cadastrado com esse e-mail.", exception.getMessage());
    }

    @Test
    public void testCadastrarProfessor_DuplicateCpf() {
        ProfessorRequest request = criarProfessorRequest("Novo Professor", "novoemail@teste.com", "novaSenha", "12345678901", "Departamento A", "1234567");
        when(professorRepository.findByCpfAndAtivoTrue(request.cpf())).thenReturn(Optional.of(new Professor()));

        Exception exception = assertThrows(RuntimeException.class, () ->
                professorService.cadastrarProfessor(request)
        );

        assertEquals("Já existe um professor ativo cadastrado com esse CPF.", exception.getMessage());
    }

    @Test
    public void testCadastrarProfessor_SaveError() {
        ProfessorRequest request = criarProfessorRequest("Novo Professor", "novoemail@teste.com", "novaSenha", "12345678901", "Departamento A", "1234567");
        Professor professor = criarProfessorPadrao();

        when(professorRepository.findByEmailAndAtivoTrue(request.email())).thenReturn(Optional.empty());
        when(professorRepository.findByCpfAndAtivoTrue(request.cpf())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.senha())).thenReturn("senhaCodificada");
        when(professorMapper.toEntity(request)).thenReturn(professor);
        when(professorRepository.save(any())).thenThrow(new RuntimeException("Erro ao salvar professor"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                professorService.cadastrarProfessor(request)
        );

        assertEquals("Erro ao cadastrar professor: Erro ao salvar professor", exception.getMessage());
    }

    @Test
    public void testCadastrarProfessor_EmptyPassword() {
        ProfessorRequest request = criarProfessorRequest("Novo Professor", "novoemail@teste.com", "", "12345678901", "Departamento A", "1234567");

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                professorService.cadastrarProfessor(request)
        );

        assertEquals("Senha não pode ser vazio.", exception.getMessage());
    }

    @Test
    public void testCadastrarProfessor_NullPassword() {
        ProfessorRequest request = criarProfessorRequest("Novo Professor", "novoemail@teste.com", null, "12345678901", "Departamento A", "1234567");

        Exception exception = assertThrows(NullPointerException.class, () ->
                professorService.cadastrarProfessor(request)
        );

        assertEquals("Senha não pode ser nulo.", exception.getMessage());
    }

    @Test
    public void testCadastrarProfessor_NullName() {
        ProfessorRequest request = criarProfessorRequest(null, "novoemail@teste.com", "novaSenha", "12345678901", "Departamento A", "1234567");

        Exception exception = assertThrows(NullPointerException.class, () ->
                professorService.cadastrarProfessor(request)
        );

        assertEquals("Nome não pode ser nulo.", exception.getMessage());
    }

    @Test
    public void testCadastrarProfessor_EmptyName() {
        ProfessorRequest request = criarProfessorRequest("", "novoemail@teste.com", "novaSenha", "12345678901", "Departamento A", "1234567");

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                professorService.cadastrarProfessor(request)
        );

        assertEquals("Nome não pode ser vazio.", exception.getMessage());
    }

    @Test
    public void testCadastrarProfessor_NullCpf() {
        ProfessorRequest request = criarProfessorRequest("Novo Professor", "novoemail@teste.com", "novaSenha", null, "Departamento A", "1234567");

        Exception exception = assertThrows(NullPointerException.class, () ->
                professorService.cadastrarProfessor(request)
        );

        assertEquals("CPF não pode ser nulo.", exception.getMessage());
    }

    @Test
    public void testCadastrarProfessor_EmptyCpf() {
        ProfessorRequest request = criarProfessorRequest("Novo Professor", "novoemail@teste.com", "novaSenha", "", "Departamento A", "1234567");

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                professorService.cadastrarProfessor(request)
        );

        assertEquals("CPF não pode ser vazio.", exception.getMessage());
    }

    @Test
    public void testCadastrarProfessor_NullDepartamento() {
        ProfessorRequest request = criarProfessorRequest("Novo Professor", "novoemail@teste.com", "novaSenha", "12345678901", null, "1234567");

        Exception exception = assertThrows(NullPointerException.class, () ->
                professorService.cadastrarProfessor(request)
        );

        assertEquals("Departamento não pode ser nulo.", exception.getMessage());
    }

    @Test
    public void testCadastrarProfessor_EmptyDepartamento() {
        ProfessorRequest request = criarProfessorRequest("Novo Professor", "novoemail@teste.com", "novaSenha", "12345678901", "", "1234567");

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                professorService.cadastrarProfessor(request)
        );

        assertEquals("Departamento não pode ser vazio.", exception.getMessage());
    }

    @Test
    public void testCadastrarProfessor_NullSiape() {
        ProfessorRequest request = criarProfessorRequest("Novo Professor", "novoemail@teste.com", "novaSenha", "12345678901", "Departamento A", null);

        Exception exception = assertThrows(NullPointerException.class, () ->
                professorService.cadastrarProfessor(request)
        );

        assertEquals("SIAPE não pode ser nulo.", exception.getMessage());
    }

    @Test
    public void testCadastrarProfessor_EmptySiape() {
        ProfessorRequest request = criarProfessorRequest("Novo Professor", "novoemail@teste.com", "novaSenha", "12345678901", "Departamento A", "");

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                professorService.cadastrarProfessor(request)
        );

        assertEquals("SIAPE não pode ser vazio.", exception.getMessage());
    }
}
