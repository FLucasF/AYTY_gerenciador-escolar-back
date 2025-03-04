package br.com.ufpb.GerenciadorEscolar.service.aluno;

import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorRequest;
import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorResponse;
import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoRequest;
import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoResponse;
import br.com.ufpb.GerenciadorEscolar.model.Administrador;
import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AlunoServiceImplCadastrarTest extends BaseAlunoServiceTest {
    @Test
    public void testCadastrarAluno_Success() {
        AlunoRequest request = new AlunoRequest("Novo Aluno", "novoemail@teste.com", "novaSenha", "12345678901", "Curso A");

        Aluno aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("Novo Aluno");
        aluno.setEmail("novoemail@teste.com");
        aluno.setCpf("12345678901");
        aluno.setCurso("Curso A");
        aluno.setSenha("senhaCodificada");
        aluno.setAtivo(true);

        when(alunoRepository.findByEmailAndAtivoTrue(request.email())).thenReturn(Optional.empty());
        when(alunoRepository.findByCpfAndAtivoTrue(request.cpf())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.senha())).thenReturn("senhaCodificada");
        when(alunoMapper.toEntity(request)).thenReturn(aluno);
        when(alunoRepository.save(any())).thenReturn(aluno);
        when(alunoMapper.toResponse(any())).thenReturn(new AlunoResponse(1L, "Novo Aluno", "novoemail@teste.com", "12345678901", "Curso A"));

        AlunoResponse response = alunoService.cadastrarAluno(request);

        assertNotNull(response);
        assertEquals("Novo Aluno", response.nome());
        assertEquals("novoemail@teste.com", response.email());
        assertEquals("Curso A", response.curso());

        verify(alunoRepository, times(1)).save(any());
    }

    @Test
    public void testCadastrarAluno_DuplicateEmail() {
        AlunoRequest request = new AlunoRequest("Novo Aluno", "emailduplicado@teste.com", "novaSenha", "12345678901", "Curso A");

        when(alunoRepository.findByEmailAndAtivoTrue(request.email())).thenReturn(Optional.of(new Aluno()));

        Exception exception = assertThrows(RuntimeException.class, () ->
                alunoService.cadastrarAluno(request)
        );

        assertEquals("Já existe um aluno ativo cadastrado com esse e-mail.", exception.getMessage());
    }

    @Test
    public void testCadastrarAluno_DuplicateCpf() {
        AlunoRequest request = new AlunoRequest("Novo Aluno", "novoemail@teste.com", "novaSenha", "12345678901", "Curso A");

        when(alunoRepository.findByEmailAndAtivoTrue(request.email())).thenReturn(Optional.empty());
        when(alunoRepository.findByCpfAndAtivoTrue(request.cpf())).thenReturn(Optional.of(new Aluno()));

        Exception exception = assertThrows(RuntimeException.class, () ->
                alunoService.cadastrarAluno(request)
        );

        assertEquals("Já existe um aluno ativo cadastrado com esse CPF.", exception.getMessage());
    }

    @Test
    public void testCadastrarAluno_SaveError() {
        AlunoRequest request = new AlunoRequest("Novo Aluno", "novoemail@teste.com", "novaSenha", "12345678901", "Curso A");

        when(alunoRepository.findByEmailAndAtivoTrue(request.email())).thenReturn(Optional.empty());
        when(alunoRepository.findByCpfAndAtivoTrue(request.cpf())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.senha())).thenReturn("senhaCodificada");
        when(alunoMapper.toEntity(request)).thenReturn(new Aluno());
        when(alunoRepository.save(any())).thenThrow(new RuntimeException("Erro ao salvar aluno"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                alunoService.cadastrarAluno(request)
        );

        assertEquals("Erro ao cadastrar aluno: Erro ao salvar aluno", exception.getMessage());
    }

    @Test
    public void testCadastrarAluno_EmptyPassword() {
        AlunoRequest request = new AlunoRequest("Novo Aluno", "novoemail@teste.com", "", "12345678901", "Curso A");

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                alunoService.cadastrarAluno(request)
        );

        assertEquals("Senha não pode ser vazio.", exception.getMessage());
    }

    @Test
    public void testCadastrarAluno_NullPassword() {
        AlunoRequest request = new AlunoRequest("Novo Aluno", "novoemail@teste.com", null, "12345678901", "Curso A");

        Exception exception = assertThrows(NullPointerException.class, () ->
                alunoService.cadastrarAluno(request)
        );

        assertEquals("Senha não pode ser nulo.", exception.getMessage());
    }

    @Test
    public void testCadastrarAluno_NullName() {
        AlunoRequest request = new AlunoRequest(null, "novoemail@teste.com", "novaSenha", "12345678901", "Curso A");

        Exception exception = assertThrows(NullPointerException.class, () ->
                alunoService.cadastrarAluno(request)
        );

        assertEquals("Nome não pode ser nulo.", exception.getMessage());
    }

    @Test
    public void testCadastrarAluno_EmptyName() {
        AlunoRequest request = new AlunoRequest("", "novoemail@teste.com", "novaSenha", "12345678901", "Curso A");

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                alunoService.cadastrarAluno(request)
        );

        assertEquals("Nome não pode ser vazio.", exception.getMessage());
    }

    @Test
    public void testCadastrarAluno_NullCpf() {
        AlunoRequest request = new AlunoRequest("Novo Aluno", "novoemail@teste.com", "novaSenha", null, "Curso A");

        Exception exception = assertThrows(NullPointerException.class, () ->
                alunoService.cadastrarAluno(request)
        );

        assertEquals("CPF não pode ser nulo.", exception.getMessage());
    }

    @Test
    public void testCadastrarAluno_EmptyCpf() {
        AlunoRequest request = new AlunoRequest("Novo Aluno", "novoemail@teste.com", "novaSenha", "", "Curso A");

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                alunoService.cadastrarAluno(request)
        );

        assertEquals("CPF não pode ser vazio.", exception.getMessage());
    }

    @Test
    public void testCadastrarAluno_NullCurso() {
        AlunoRequest request = new AlunoRequest("Novo Aluno", "novoemail@teste.com", "novaSenha", "12345678901", null);

        Exception exception = assertThrows(NullPointerException.class, () ->
                alunoService.cadastrarAluno(request)
        );

        assertEquals("Curso não pode ser nulo.", exception.getMessage());
    }

    @Test
    public void testCadastrarAluno_EmptyCurso() {
        AlunoRequest request = new AlunoRequest("Novo Aluno", "novoemail@teste.com", "novaSenha", "12345678901", "");

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                alunoService.cadastrarAluno(request)
        );

        assertEquals("Curso não pode ser vazio.", exception.getMessage());
    }






}
