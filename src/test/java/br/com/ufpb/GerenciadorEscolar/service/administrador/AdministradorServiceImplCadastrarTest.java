package br.com.ufpb.GerenciadorEscolar.service.administrador;

import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorRequest;
import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorResponse;
import br.com.ufpb.GerenciadorEscolar.model.Administrador;
import br.com.ufpb.GerenciadorEscolar.model.UserLogin;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

public class AdministradorServiceImplCadastrarTest extends BaseAdministradorServiceTest {

    // ✅ Teste de cadastro bem-sucedido
    @Test
    public void testCadastrarAdministrador_Success() {
        AdministradorRequest request = new AdministradorRequest(
                "Nome", "email@teste.com", "senha123", "cpf123", "setor", "1234567"
        );
        Administrador admin = new Administrador();
        admin.setId(1L);
        UserLogin userLogin = new UserLogin("email@teste.com", "senha123", admin);

        when(administradorMapper.toEntity(request)).thenReturn(admin);
        when(passwordEncoder.encode("senha123")).thenReturn("senhaCodificada");
        when(administradorRepository.findByEmailAndAtivoTrue("email@teste.com")).thenReturn(Optional.empty());
        when(administradorRepository.findByCpfAndAtivoTrue("cpf123")).thenReturn(Optional.empty());
        when(administradorRepository.save(admin)).thenReturn(admin);
        when(userLoginRepository.save(any(UserLogin.class))).thenReturn(userLogin);

        AdministradorResponse response = new AdministradorResponse(
                admin.getId(), "Nome", "email@teste.com", "cpf123", "setor", "1234567"
        );
        when(administradorMapper.toResponse(admin)).thenReturn(response);

        AdministradorResponse result = administradorService.cadastrarAdministrador(request);

        assertNotNull(result);
        assertEquals(admin.getId(), result.id());

        verify(administradorRepository, times(1)).save(admin);
        verify(userLoginRepository, times(1)).save(any(UserLogin.class));
    }

    // ✅ Testes de valores NULL
    @Test
    public void testCadastrarAdministrador_NullNome() {
        AdministradorRequest request = new AdministradorRequest(
                null, "email@teste.com", "senha123", "cpf123", "setor", "1234567"
        );

        Exception exception = assertThrows(NullPointerException.class, () ->
                administradorService.cadastrarAdministrador(request)
        );

        assertEquals("Nome não pode ser nulo.", exception.getMessage());
        verify(userLoginRepository, never()).save(any());
    }

    @Test
    public void testCadastrarAdministrador_NullEmail() {
        AdministradorRequest request = new AdministradorRequest(
                "Nome", null, "senha123", "cpf123", "setor", "1234567"
        );

        Exception exception = assertThrows(NullPointerException.class, () ->
                administradorService.cadastrarAdministrador(request)
        );

        assertEquals("Email não pode ser nulo.", exception.getMessage());
        verify(userLoginRepository, never()).save(any());
    }

    @Test
    public void testCadastrarAdministrador_NullSenha() {
        AdministradorRequest request = new AdministradorRequest(
                "Nome", "email@teste.com", null, "cpf123", "setor", "1234567"
        );

        Exception exception = assertThrows(NullPointerException.class, () ->
                administradorService.cadastrarAdministrador(request)
        );

        assertEquals("Senha não pode ser nulo.", exception.getMessage());
        verify(userLoginRepository, never()).save(any());
    }

    @Test
    public void testCadastrarAdministrador_NullCpf() {
        AdministradorRequest request = new AdministradorRequest(
                "Nome", "email@teste.com", "senha123", null, "setor", "1234567"
        );

        Exception exception = assertThrows(NullPointerException.class, () ->
                administradorService.cadastrarAdministrador(request)
        );

        assertEquals("CPF não pode ser nulo.", exception.getMessage());
        verify(userLoginRepository, never()).save(any());
    }

    @Test
    public void testCadastrarAdministrador_NullSetor() {
        AdministradorRequest request = new AdministradorRequest(
                "Nome", "email@teste.com", "senha123", "cpf123", null, "1234567"
        );

        Exception exception = assertThrows(NullPointerException.class, () ->
                administradorService.cadastrarAdministrador(request)
        );

        assertEquals("Setor não pode ser nulo.", exception.getMessage());
        verify(userLoginRepository, never()).save(any());
    }

    @Test
    public void testCadastrarAdministrador_NullSiape() {
        AdministradorRequest request = new AdministradorRequest(
                "Nome", "email@teste.com", "senha123", "cpf123", "setor", null
        );

        Exception exception = assertThrows(NullPointerException.class, () ->
                administradorService.cadastrarAdministrador(request)
        );

        assertEquals("SIAPE não pode ser nulo.", exception.getMessage());
        verify(userLoginRepository, never()).save(any());
    }

    // ✅ Testes de valores vazios ("")
    @Test
    public void testCadastrarAdministrador_EmptyNome() {
        AdministradorRequest request = new AdministradorRequest(
                "", "email@teste.com", "senha123", "cpf123", "setor", "1234567"
        );

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                administradorService.cadastrarAdministrador(request)
        );

        assertEquals("Nome não pode ser vazio.", exception.getMessage());
        verify(userLoginRepository, never()).save(any());
    }

    @Test
    public void testCadastrarAdministrador_EmptyEmail() {
        AdministradorRequest request = new AdministradorRequest(
                "Nome", "", "senha123", "cpf123", "setor", "1234567"
        );

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                administradorService.cadastrarAdministrador(request)
        );

        assertEquals("Email não pode ser vazio.", exception.getMessage());
        verify(userLoginRepository, never()).save(any());
    }

    @Test
    public void testCadastrarAdministrador_EmptySenha() {
        AdministradorRequest request = new AdministradorRequest(
                "Nome", "email@teste.com", "", "cpf123", "setor", "1234567"
        );

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                administradorService.cadastrarAdministrador(request)
        );

        assertEquals("Senha não pode ser vazio.", exception.getMessage());
        verify(userLoginRepository, never()).save(any());
    }

    @Test
    public void testCadastrarAdministrador_EmptyCpf() {
        AdministradorRequest request = new AdministradorRequest(
                "Nome", "email@teste.com", "senha123", "", "setor", "1234567"
        );

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                administradorService.cadastrarAdministrador(request)
        );

        assertEquals("CPF não pode ser vazio.", exception.getMessage());
        verify(userLoginRepository, never()).save(any());
    }

    @Test
    public void testCadastrarAdministrador_EmptySetor() {
        AdministradorRequest request = new AdministradorRequest(
                "Nome", "email@teste.com", "senha123", "cpf123", "", "1234567"
        );

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                administradorService.cadastrarAdministrador(request)
        );

        assertEquals("Setor não pode ser vazio.", exception.getMessage());
        verify(userLoginRepository, never()).save(any());
    }

    @Test
    public void testCadastrarAdministrador_EmptySiape() {
        AdministradorRequest request = new AdministradorRequest(
                "Nome", "email@teste.com", "senha123", "cpf123", "setor", ""
        );

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                administradorService.cadastrarAdministrador(request)
        );

        assertEquals("SIAPE não pode ser vazio.", exception.getMessage());
        verify(userLoginRepository, never()).save(any());
    }
}
