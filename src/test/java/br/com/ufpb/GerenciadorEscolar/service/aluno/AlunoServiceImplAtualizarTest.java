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

@Slf4j
public class AlunoServiceImplAtualizarTest extends BaseAlunoServiceTest {

    // ✅ Teste de atualização bem-sucedida
    @Test
    public void testAtualizarAluno_Success() {
        Long id = 1L;
        AlunoRequest request = new AlunoRequest("Novo Nome", "novoemail@teste.com", "novocpf", "novasenha", "Novo Curso");

        Aluno aluno = new Aluno();
        aluno.setId(id);
        aluno.setAtivo(true);
        aluno.setSenha("senhaAntiga");

        mockFindById(id, aluno);
        when(alunoRepository.findByEmailAndAtivoTrue("novoemail@teste.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("novasenha")).thenReturn("senhaNovaCodificada");
        mockSave(aluno);

        AlunoResponse response = new AlunoResponse(
                id, "Novo Nome", "novoemail@teste.com", "novocpf", "Novo Curso"
        );
        when(alunoMapper.toResponse(aluno)).thenReturn(response);

        AlunoResponse result = alunoService.atualizarAluno(id, request);

        assertNotNull(result);
        assertEquals("Novo Nome", result.nome());
        assertEquals("novoemail@teste.com", result.email());
        assertEquals("Novo Curso", result.curso());

        verify(alunoRepository, times(1)).findByIdAndAtivoTrue(id);
        verify(alunoRepository, times(1)).save(aluno);
    }

    // ❌ Teste: Aluno não encontrado
    @Test
    public void testAtualizarAluno_NotFound() {
        Long id = 1L;
        AlunoRequest request = new AlunoRequest("Nome", "email@teste.com", "cpf123", "senha123", "Curso");

        when(alunoRepository.findByIdAndAtivoTrue(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                alunoService.atualizarAluno(id, request)
        );
        assertEquals("Aluno não encontrado ou inativo.", exception.getMessage());
    }

    // ❌ Teste: ID nulo deve lançar exceção
    @Test
    public void testAtualizarAluno_NullId() {
        AlunoRequest request = new AlunoRequest("Nome", "email@teste.com", "cpf123", "senha123", "Curso");

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                alunoService.atualizarAluno(null, request)
        );
        assertEquals("O ID do aluno não pode ser nulo.", exception.getMessage());
    }

    // ❌ Teste: Atualizar com email duplicado deve falhar
    @Test
    public void testAtualizarAluno_DuplicateEmail() {
        Long id = 1L;
        AlunoRequest request = new AlunoRequest("Nome", "duplicado@teste.com", "cpf123", "senha123", "Curso");

        Aluno alunoExistente = new Aluno();
        alunoExistente.setId(2L); // Simulando um aluno diferente com o mesmo email

        when(alunoRepository.findByIdAndAtivoTrue(id)).thenReturn(Optional.of(new Aluno()));
        when(alunoRepository.findByEmailAndAtivoTrue("duplicado@teste.com")).thenReturn(Optional.of(alunoExistente));

        Exception exception = assertThrows(RuntimeException.class, () ->
                alunoService.atualizarAluno(id, request)
        );
        assertEquals("Já existe outro aluno ativo cadastrado com esse e-mail.", exception.getMessage());
    }

    @Test
    public void testAtualizarAluno_KeepOldPassword() {
        Long id = 1L;
        AlunoRequest request = new AlunoRequest(
                "Nome", "email@teste.com", "", "", "Curso" // Senha vazia (deve manter a antiga)
        );

        Aluno aluno = new Aluno();
        aluno.setId(id);
        aluno.setAtivo(true);
        aluno.setSenha("senhaAntiga"); // Senha original

        log.info("🔄 Simulando a busca do aluno no banco...");
        when(alunoRepository.findByIdAndAtivoTrue(id)).thenReturn(Optional.of(aluno));

        log.info("🔄 Simulando a conversão para Response...");
        when(alunoMapper.toResponse(any())).thenReturn(new AlunoResponse(
                id, "Nome", "email@teste.com", "cpf123", "Curso"
        ));

        // 🔍 Captura o aluno salvo para garantir que a senha foi mantida
        ArgumentCaptor<Aluno> alunoCaptor = ArgumentCaptor.forClass(Aluno.class);
        when(alunoRepository.save(alunoCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        // ✅ Executa o método de atualização
        log.info("⚡️ Executando a atualização do aluno...");
        AlunoResponse result = alunoService.atualizarAluno(id, request);

        // ✅ Agora, pegamos o aluno que foi realmente salvo
        Aluno alunoSalvo = alunoCaptor.getValue();

        // ✅ Verifica se a senha foi realmente mantida
        log.info("🔍 Verificando se a senha permaneceu inalterada...");
        assertNotNull(result);
        assertEquals("senhaAntiga", alunoSalvo.getSenha(), "❌ A senha deveria permanecer inalterada!");

        // ✅ O repositório deve ter salvo corretamente o aluno
        verify(alunoRepository, times(1)).save(alunoSalvo);
        log.info("✅ Teste finalizado com sucesso. O aluno foi atualizado e a senha manteve-se inalterada.");
    }




    // ❌ Teste: Falha ao salvar no banco deve lançar exceção
    @Test
    public void testAtualizarAluno_SaveError() {
        Long id = 1L;
        AlunoRequest request = new AlunoRequest("Nome", "email@teste.com", "cpf123", "senha123", "Curso");

        Aluno aluno = new Aluno();
        aluno.setId(id);
        aluno.setAtivo(true);

        mockFindById(id, aluno);
        when(alunoRepository.save(any())).thenThrow(new RuntimeException("Erro ao atualizar aluno"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                alunoService.atualizarAluno(id, request)
        );

        assertEquals("Erro ao atualizar aluno", exception.getMessage());
    }

    // ✅ Teste: Atualizar apenas o nome
    @Test
    public void testAtualizarAluno_UpdateName() {
        Long id = 1L;
        AlunoRequest request = new AlunoRequest("Novo Nome", "email@teste.com", "cpf123", "", "Curso");

        Aluno aluno = new Aluno();
        aluno.setId(id);
        aluno.setAtivo(true);
        aluno.setNome("Nome Antigo");
        aluno.setEmail("email@teste.com");
        aluno.setCpf("cpf123");
        aluno.setCurso("Curso");

        mockFindById(id, aluno);
        mockSave(aluno);

        alunoService.atualizarAluno(id, request);

        assertEquals("Novo Nome", aluno.getNome());
    }

    // ✅ Teste: Atualizar apenas o email
    @Test
    public void testAtualizarAluno_UpdateEmail() {
        Long id = 1L;
        AlunoRequest request = new AlunoRequest("Nome", "novoemail@teste.com", "cpf123", "", "Curso");

        Aluno aluno = new Aluno();
        aluno.setId(id);
        aluno.setAtivo(true);
        aluno.setNome("Nome");
        aluno.setEmail("email@teste.com");
        aluno.setCpf("cpf123");
        aluno.setCurso("Curso");

        mockFindById(id, aluno);
        when(alunoRepository.findByEmailAndAtivoTrue("novoemail@teste.com")).thenReturn(Optional.empty());
        mockSave(aluno);

        alunoService.atualizarAluno(id, request);

        assertEquals("novoemail@teste.com", aluno.getEmail());
    }

    // ✅ Teste: Atualizar apenas o CPF
    @Test
    public void testAtualizarAluno_UpdateCpf() {
        Long id = 1L;
        AlunoRequest request = new AlunoRequest("Nome", "email@teste.com", "", "novoCPF", "Curso");

        Aluno aluno = new Aluno();
        aluno.setId(id);
        aluno.setAtivo(true);
        aluno.setNome("Nome");
        aluno.setEmail("email@teste.com");
        aluno.setCpf("cpf123");
        aluno.setCurso("Curso");

        mockFindById(id, aluno);
        mockSave(aluno);

        alunoService.atualizarAluno(id, request);

        assertEquals("novoCPF", aluno.getCpf());
    }

    // ✅ Teste: Atualizar apenas o curso
    @Test
    public void testAtualizarAluno_UpdateCurso() {
        Long id = 1L;
        AlunoRequest request = new AlunoRequest("Nome", "email@teste.com", "cpf123", "", "Novo Curso");

        Aluno aluno = new Aluno();
        aluno.setId(id);
        aluno.setAtivo(true);
        aluno.setNome("Nome");
        aluno.setEmail("email@teste.com");
        aluno.setCpf("cpf123");
        aluno.setCurso("Curso Antigo");

        mockFindById(id, aluno);
        mockSave(aluno);

        alunoService.atualizarAluno(id, request);

        assertEquals("Novo Curso", aluno.getCurso());
    }

    // ✅ Teste: Atualizar apenas a senha
    @Test
    public void testAtualizarAluno_UpdatePassword() {
        Long id = 1L;
        AlunoRequest request = new AlunoRequest("Nome", "email@teste.com", "novaSenha", "cpf123", "Curso");

        Aluno aluno = new Aluno();
        aluno.setId(id);
        aluno.setAtivo(true);
        aluno.setNome("Nome");
        aluno.setEmail("email@teste.com");
        aluno.setCpf("cpf123");
        aluno.setCurso("Curso");
        aluno.setSenha("senhaAntiga");

        mockFindById(id, aluno);

        // Simula a codificação da nova senha
        when(passwordEncoder.encode("novaSenha")).thenReturn("senhaNovaCodificada");

        // Captura o aluno salvo no repositório para verificar a senha alterada
        ArgumentCaptor<Aluno> alunoCaptor = ArgumentCaptor.forClass(Aluno.class);
        when(alunoRepository.save(alunoCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        // Executa a atualização
        alunoService.atualizarAluno(id, request);

        // Obtém o aluno salvo
        Aluno alunoSalvo = alunoCaptor.getValue();

        // Verifica se a senha foi corretamente atualizada
        assertNotNull(alunoSalvo);
        assertEquals("senhaNovaCodificada", alunoSalvo.getSenha(), "❌ A senha não foi corretamente codificada e salva!");
    }

    @Test
    public void testAtualizarAluno_KeepOldName() {
        Long id = 1L;
        AlunoRequest request = new AlunoRequest(null, "email@teste.com", "novaSenha", "cpf123", "Curso");

        Aluno aluno = new Aluno();
        aluno.setId(id);
        aluno.setAtivo(true);
        aluno.setNome("Nome Antigo");
        aluno.setEmail("email@teste.com");
        aluno.setCpf("cpf123");
        aluno.setCurso("Curso");
        aluno.setSenha("senhaAntiga");

        mockFindById(id, aluno);
        mockSave(aluno);

        alunoService.atualizarAluno(id, request);

        assertEquals("Nome Antigo", aluno.getNome(), "❌ O nome deveria permanecer inalterado!");
    }

    @Test
    public void testAtualizarAluno_KeepOldEmail() {
        Long id = 1L;
        AlunoRequest request = new AlunoRequest("Nome", null, "novaSenha", "cpf123", "Curso");

        Aluno aluno = new Aluno();
        aluno.setId(id);
        aluno.setAtivo(true);
        aluno.setNome("Nome");
        aluno.setEmail("email@teste.com");
        aluno.setCpf("cpf123");
        aluno.setCurso("Curso");
        aluno.setSenha("senhaAntiga");

        mockFindById(id, aluno);
        mockSave(aluno);

        alunoService.atualizarAluno(id, request);

        assertEquals("email@teste.com", aluno.getEmail(), "❌ O e-mail deveria permanecer inalterado!");
    }

    @Test
    public void testAtualizarAluno_KeepOldCpf() {
        Long id = 1L;
        AlunoRequest request = new AlunoRequest("Nome", "email@teste.com", "novaSenha", null, "Curso");

        Aluno aluno = new Aluno();
        aluno.setId(id);
        aluno.setAtivo(true);
        aluno.setNome("Nome");
        aluno.setEmail("email@teste.com");
        aluno.setCpf("cpf123");
        aluno.setCurso("Curso");
        aluno.setSenha("senhaAntiga");

        mockFindById(id, aluno);
        mockSave(aluno);

        alunoService.atualizarAluno(id, request);

        assertEquals("cpf123", aluno.getCpf(), "❌ O CPF deveria permanecer inalterado!");
    }

    @Test
    public void testAtualizarAluno_KeepOldCurso() {
        Long id = 1L;
        AlunoRequest request = new AlunoRequest("Nome", "email@teste.com", "novaSenha", "cpf123", null);

        Aluno aluno = new Aluno();
        aluno.setId(id);
        aluno.setAtivo(true);
        aluno.setNome("Nome");
        aluno.setEmail("email@teste.com");
        aluno.setCpf("cpf123");
        aluno.setCurso("Curso Antigo");
        aluno.setSenha("senhaAntiga");

        mockFindById(id, aluno);
        mockSave(aluno);

        alunoService.atualizarAluno(id, request);

        assertEquals("Curso Antigo", aluno.getCurso(), "❌ O curso deveria permanecer inalterado!");
    }

    @Test
    public void testAtualizarAluno_KeepOldPassword_Null() {
        Long id = 1L;
        AlunoRequest request = new AlunoRequest("Nome", "email@teste.com", null, "cpf123", "Curso"); // Senha nula

        Aluno aluno = new Aluno();
        aluno.setId(id);
        aluno.setAtivo(true);
        aluno.setSenha("senhaAntiga"); // Senha original

        mockFindById(id, aluno);
        mockSave(aluno);

        alunoService.atualizarAluno(id, request);

        assertEquals("senhaAntiga", aluno.getSenha(), "❌ A senha deveria permanecer inalterada quando for nula!");
    }

    @Test
    public void testAtualizarAluno_KeepOldName_Null() {
        Long id = 1L;
        AlunoRequest request = new AlunoRequest(null, "email@teste.com", "senhaNova", "cpf123", "Curso");

        Aluno aluno = new Aluno();
        aluno.setId(id);
        aluno.setAtivo(true);
        aluno.setNome("Nome Antigo"); // Nome original
        aluno.setEmail("email@teste.com");
        aluno.setCpf("cpf123");
        aluno.setCurso("Curso");

        mockFindById(id, aluno);
        mockSave(aluno);

        alunoService.atualizarAluno(id, request);

        assertEquals("Nome Antigo", aluno.getNome(), "❌ O nome deveria permanecer inalterado!");
    }

    @Test
    public void testAtualizarAluno_KeepOldEmail_Null() {
        Long id = 1L;
        AlunoRequest request = new AlunoRequest("Nome", null, "senhaNova", "cpf123", "Curso");

        Aluno aluno = new Aluno();
        aluno.setId(id);
        aluno.setAtivo(true);
        aluno.setNome("Nome");
        aluno.setEmail("email@teste.com"); // Email original
        aluno.setCpf("cpf123");
        aluno.setCurso("Curso");

        mockFindById(id, aluno);
        mockSave(aluno);

        alunoService.atualizarAluno(id, request);

        assertEquals("email@teste.com", aluno.getEmail(), "❌ O e-mail deveria permanecer inalterado!");
    }

    @Test
    public void testAtualizarAluno_KeepOldCpf_Null() {
        Long id = 1L;
        AlunoRequest request = new AlunoRequest("Nome", "email@teste.com", "senhaNova", null, "Curso");

        Aluno aluno = new Aluno();
        aluno.setId(id);
        aluno.setAtivo(true);
        aluno.setNome("Nome");
        aluno.setEmail("email@teste.com");
        aluno.setCpf("cpf123"); // CPF original
        aluno.setCurso("Curso");

        mockFindById(id, aluno);
        mockSave(aluno);

        alunoService.atualizarAluno(id, request);

        assertEquals("cpf123", aluno.getCpf(), "❌ O CPF deveria permanecer inalterado!");
    }

    @Test
    public void testAtualizarAluno_KeepOldCurso_Null() {
        Long id = 1L;
        AlunoRequest request = new AlunoRequest("Nome", "email@teste.com", "senhaNova", "cpf123", null);

        Aluno aluno = new Aluno();
        aluno.setId(id);
        aluno.setAtivo(true);
        aluno.setNome("Nome");
        aluno.setEmail("email@teste.com");
        aluno.setCpf("cpf123");
        aluno.setCurso("Curso Antigo"); // Curso original

        mockFindById(id, aluno);
        mockSave(aluno);

        alunoService.atualizarAluno(id, request);

        assertEquals("Curso Antigo", aluno.getCurso(), "❌ O curso deveria permanecer inalterado!");
    }
}
