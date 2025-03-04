package br.com.ufpb.GerenciadorEscolar.service.administrador;

import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorRequest;
import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorResponse;
import br.com.ufpb.GerenciadorEscolar.model.Administrador;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdministradorServiceImplCadastrarTest extends BaseAdministradorServiceTest {

    // Cenário de sucesso
    @Test
    public void testCadastrarAdministrador_Success() {
        AdministradorRequest request = new AdministradorRequest(
                "Nome", "email@teste.com", "senha123", "cpf123", "setor", "1234567"
        );
        Administrador admin = new Administrador();
        admin.setId(1L);

        when(administradorMapper.toEntity(request)).thenReturn(admin);
        when(passwordEncoder.encode("senha123")).thenReturn("senhaCodificada");
        when(administradorRepository.findByEmailAndAtivoTrue("email@teste.com"))
                .thenReturn(Optional.empty());
        when(administradorRepository.findByCpfAndAtivoTrue("cpf123"))
                .thenReturn(Optional.empty());
        when(administradorRepository.save(admin)).thenReturn(admin);

        AdministradorResponse response = new AdministradorResponse(
                admin.getId(), "Nome", "email@teste.com", "cpf123", "setor", "1234567"
        );
        when(administradorMapper.toResponse(admin)).thenReturn(response);

        AdministradorResponse result = administradorService.cadastrarAdministrador(request);

        assertNotNull(result);
        assertEquals(admin.getId(), result.id());
        verify(administradorRepository, times(1)).save(admin);
    }

    // Cenário de duplicidade: e-mail
    @Test
    public void testCadastrarAdministrador_DuplicateEmail() {
        AdministradorRequest request = new AdministradorRequest(
                "Nome", "email@teste.com", "senha123", "cpf123", "setor", "1234567"
        );
        Administrador adminExistente = new Administrador();
        when(administradorRepository.findByEmailAndAtivoTrue("email@teste.com"))
                .thenReturn(Optional.of(adminExistente));

        Exception exception = assertThrows(RuntimeException.class, () ->
                administradorService.cadastrarAdministrador(request)
        );
        assertEquals("Já existe um administrador ativo cadastrado com esse e-mail.", exception.getMessage());
    }

    // Cenário de duplicidade: CPF
    @Test
    public void testCadastrarAdministrador_DuplicateCPF() {
        AdministradorRequest request = new AdministradorRequest(
                "Nome", "email@teste.com", "senha123", "cpf123", "setor", "1234567"
        );
        when(administradorRepository.findByEmailAndAtivoTrue("email@teste.com"))
                .thenReturn(Optional.empty());
        Administrador adminExistente = new Administrador();
        when(administradorRepository.findByCpfAndAtivoTrue("cpf123"))
                .thenReturn(Optional.of(adminExistente));

        Exception exception = assertThrows(RuntimeException.class, () ->
                administradorService.cadastrarAdministrador(request)
        );
        assertEquals("Já existe um administrador ativo cadastrado com esse CPF.", exception.getMessage());
    }

    // Cenário de falha ao salvar a entidade
    @Test
    public void testCadastrarAdministrador_SaveFailure() {
        AdministradorRequest request = new AdministradorRequest(
                "Nome", "email@teste.com", "senha123", "cpf123", "setor", "1234567"
        );
        Administrador admin = new Administrador();
        admin.setId(1L);

        when(administradorMapper.toEntity(request)).thenReturn(admin);
        when(passwordEncoder.encode("senha123")).thenReturn("senhaCodificada");
        when(administradorRepository.findByEmailAndAtivoTrue("email@teste.com"))
                .thenReturn(Optional.empty());
        when(administradorRepository.findByCpfAndAtivoTrue("cpf123"))
                .thenReturn(Optional.empty());

        RuntimeException saveException = new RuntimeException("Erro no banco");
        when(administradorRepository.save(admin)).thenThrow(saveException);

        Exception exception = assertThrows(RuntimeException.class, () ->
                administradorService.cadastrarAdministrador(request)
        );
        assertTrue(exception.getMessage().contains("Erro ao cadastrar administrador: Erro no banco"));
    }

    @Test
    public void testCadastrarAdministrador_NullNome() {
        AdministradorRequest request = new AdministradorRequest(
                null, "email@teste.com", "senha123", "cpf123", "setor", "1234567"
        );
        when(administradorMapper.toEntity(request))
                .thenThrow(new NullPointerException("Nome não pode ser vazio"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                administradorService.cadastrarAdministrador(request)
        );
        assertEquals("Nome não pode ser nulo.", exception.getMessage());
    }

    @Test
    public void testCadastrarAdministrador_EmptyNome() {
        AdministradorRequest request = new AdministradorRequest(
                "", "email@teste.com", "senha123", "cpf123", "setor", "1234567"
        );
        when(administradorMapper.toEntity(request))
                .thenThrow(new IllegalArgumentException("Nome não pode ser vazio"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                administradorService.cadastrarAdministrador(request)
        );
        assertEquals("Nome não pode ser vazio.", exception.getMessage());
    }

    @Test
    public void testCadastrarAdministrador_NullEmail() {
        AdministradorRequest request = new AdministradorRequest(
                "Nome", null, "senha123", "cpf123", "setor", "1234567"
        );
        when(administradorMapper.toEntity(request))
                .thenThrow(new NullPointerException("Email não pode ser vazio"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                administradorService.cadastrarAdministrador(request)
        );
        assertEquals("Email não pode ser nulo.", exception.getMessage());
    }

    @Test
    public void testCadastrarAdministrador_EmptyEmail() {
        AdministradorRequest request = new AdministradorRequest(
                "Nome", "", "senha123", "cpf123", "setor", "1234567"
        );
        when(administradorMapper.toEntity(request))
                .thenThrow(new IllegalArgumentException("Email não pode ser vazio"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                administradorService.cadastrarAdministrador(request)
        );
        assertEquals("Email não pode ser vazio.", exception.getMessage());
    }

    @Test
    public void testCadastrarAdministrador_NullSenha() {
        AdministradorRequest request = new AdministradorRequest(
                "Nome", "email@teste.com", null, "cpf123", "setor", "1234567"
        );
        // Supondo que o mapper converte o request para entidade sem problemas,
        // mas a codificação da senha lança exceção
        when(administradorMapper.toEntity(request)).thenReturn(new Administrador());
        when(passwordEncoder.encode(null))
                .thenThrow(new NullPointerException("Senha não pode ser nula"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                administradorService.cadastrarAdministrador(request)
        );
        assertEquals("Senha não pode ser nulo.", exception.getMessage());
    }

    @Test
    public void testCadastrarAdministrador_EmptySenha() {
        AdministradorRequest request = new AdministradorRequest(
                "Nome", "email@teste.com", "", "cpf123", "setor", "1234567"
        );
        when(administradorMapper.toEntity(request)).thenReturn(new Administrador());
        when(passwordEncoder.encode("")).thenThrow(new RuntimeException("Senha não pode ser vazia"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                administradorService.cadastrarAdministrador(request)
        );
        assertEquals("Senha não pode ser vazio.", exception.getMessage());
    }

    @Test
    public void testCadastrarAdministrador_NullCpf() {
        AdministradorRequest request = new AdministradorRequest(
                "Nome", "email@teste.com", "senha123", null, "setor", "1234567"
        );
        when(administradorMapper.toEntity(request))
                .thenThrow(new NullPointerException("CPF não pode ser nulo"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                administradorService.cadastrarAdministrador(request)
        );
        assertEquals("CPF não pode ser nulo.", exception.getMessage());
    }

    @Test
    public void testCadastrarAdministrador_EmptyCpf() {
        AdministradorRequest request = new AdministradorRequest(
                "Nome", "email@teste.com", "senha123", "", "setor", "1234567"
        );
        when(administradorMapper.toEntity(request))
                .thenThrow(new IllegalArgumentException("CPF não pode ser vazio"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                administradorService.cadastrarAdministrador(request)
        );
        assertEquals("CPF não pode ser vazio.", exception.getMessage());
    }

    @Test
    public void testCadastrarAdministrador_NullSetor() {
        AdministradorRequest request = new AdministradorRequest(
                "Nome", "email@teste.com", "senha123", "cpf123", null, "1234567"
        );
        when(administradorMapper.toEntity(request))
                .thenThrow(new NullPointerException("Setor não pode ser nulo."));

        Exception exception = assertThrows(RuntimeException.class, () ->
                administradorService.cadastrarAdministrador(request)
        );
        assertEquals("Setor não pode ser nulo.", exception.getMessage());
    }

    @Test
    public void testCadastrarAdministrador_EmptySetor() {
        AdministradorRequest request = new AdministradorRequest(
                "Nome", "email@teste.com", "senha123", "cpf123", "", "1234567"
        );
        when(administradorMapper.toEntity(request))
                .thenThrow(new IllegalArgumentException("Setor não pode ser vazio"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                administradorService.cadastrarAdministrador(request)
        );
        assertEquals("Setor não pode ser vazio.", exception.getMessage());
    }

    @Test
    public void testCadastrarAdministrador_NullSiape() {
        AdministradorRequest request = new AdministradorRequest(
                "Nome", "email@teste.com", "senha123", "cpf123", "setor", null
        );
        when(administradorMapper.toEntity(request))
                .thenThrow(new NullPointerException("SIAPE não pode ser nulo."));

        Exception exception = assertThrows(RuntimeException.class, () ->
                administradorService.cadastrarAdministrador(request)
        );
        assertEquals("SIAPE não pode ser nulo.", exception.getMessage());
    }

    @Test
    public void testCadastrarAdministrador_EmptySiape() {
        AdministradorRequest request = new AdministradorRequest(
                "Nome", "email@teste.com", "senha123", "cpf123", "setor", ""
        );
        when(administradorMapper.toEntity(request))
                .thenThrow(new IllegalArgumentException("SIAPE não pode ser vazio"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                administradorService.cadastrarAdministrador(request)
        );
        assertEquals("SIAPE não pode ser vazio.", exception.getMessage());
    }
}
