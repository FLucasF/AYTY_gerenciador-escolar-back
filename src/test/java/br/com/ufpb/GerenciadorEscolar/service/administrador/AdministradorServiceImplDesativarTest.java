package br.com.ufpb.GerenciadorEscolar.service.administrador;

import br.com.ufpb.GerenciadorEscolar.model.Administrador;
import br.com.ufpb.GerenciadorEscolar.model.UserLogin;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdministradorServiceImplDesativarTest extends BaseAdministradorServiceTest {


    @Test
    public void testDesativarAdministrador_Success() {
        Long id = 1L;
        Administrador admin = new Administrador();
        admin.setId(id);

        UserLogin userLogin = new UserLogin();

        // Simula a busca do administrador e do UserLogin associado
        when(administradorRepository.findByIdAndAtivoTrue(id)).thenReturn(Optional.of(admin));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(admin)).thenReturn(Optional.of(userLogin));

        // Executa o método de desativação
        administradorService.desativarAdministrador(id);

        // Verifica se o administrador e o UserLogin foram desativados corretamente
        assertFalse(admin.isAtivo());
        assertFalse(userLogin.isAtivo());

        // Verifica se os métodos de salvar foram chamados corretamente
        verify(administradorRepository, times(1)).save(admin);
        verify(userLoginRepository, times(1)).save(userLogin);
    }

    @Test
    public void testDesativarAdministrador_NotFound() {
        Long id = 1L;

        // Simula que não existe um administrador ativo com o ID informado
        when(administradorRepository.findByIdAndAtivoTrue(id)).thenReturn(Optional.empty());

        // Verifica se o método lança a exceção com a mensagem esperada
        Exception exception = assertThrows(RuntimeException.class, () ->
                administradorService.desativarAdministrador(id)
        );
        assertEquals("Administrador não encontrado", exception.getMessage());

        // Verifica que os repositórios NÃO foram chamados
        verify(administradorRepository, never()).save(any());
        verify(userLoginRepository, never()).save(any());
    }

    @Test
    public void testDesativarAdministrador_AlreadyInactive() {
        Long id = 1L;
        Administrador admin = new Administrador();
        admin.setId(id);

        // Simula que o administrador já está desativado
        when(administradorRepository.findByIdAndAtivoTrue(id)).thenReturn(Optional.empty());

        // Deve lançar a exceção pois o administrador já está inativo
        Exception exception = assertThrows(RuntimeException.class, () ->
                administradorService.desativarAdministrador(id)
        );
        assertEquals("Administrador não encontrado", exception.getMessage());

        // Verifica que os repositórios NÃO foram chamados
        verify(administradorRepository, never()).save(any());
        verify(userLoginRepository, never()).save(any());
    }

    @Test
    public void testDesativarAdministrador_UserLoginNotFound() {
        Long id = 1L;
        Administrador admin = new Administrador();
        admin.setId(id);

        // Simula que existe um administrador ativo, mas não há UserLogin associado
        when(administradorRepository.findByIdAndAtivoTrue(id)).thenReturn(Optional.of(admin));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(admin)).thenReturn(Optional.empty());

        // Executa a desativação mesmo sem UserLogin
        administradorService.desativarAdministrador(id);

        // Verifica se apenas o administrador foi desativado
        assertFalse(admin.isAtivo());

        // Verifica se o administrador foi salvo, mas o UserLogin não
        verify(administradorRepository, times(1)).save(admin);
        verify(userLoginRepository, never()).save(any());
    }

    @Test
    public void testDesativarAdministrador_FailureSavingAdmin() {
        Long id = 1L;
        Administrador admin = new Administrador();
        admin.setId(id);

        when(administradorRepository.findByIdAndAtivoTrue(id)).thenReturn(Optional.of(admin));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(admin)).thenReturn(Optional.of(new UserLogin()));

        // Simula falha ao salvar no repositório do Administrador
        doThrow(new RuntimeException("Erro ao salvar administrador")).when(administradorRepository).save(admin);

        Exception exception = assertThrows(RuntimeException.class, () ->
                administradorService.desativarAdministrador(id)
        );

        assertEquals("Erro ao salvar administrador", exception.getMessage());

        // Confirma que a tentativa de salvar o UserLogin ocorreu antes da falha no admin
        verify(userLoginRepository, times(1)).save(any(UserLogin.class));
        verify(administradorRepository, times(1)).save(admin);
    }

    @Test
    public void testDesativarAdministrador_FailureSavingUserLogin() {
        Long id = 1L;
        Administrador admin = new Administrador();
        admin.setId(id);
        UserLogin userLogin = new UserLogin();

        when(administradorRepository.findByIdAndAtivoTrue(id)).thenReturn(Optional.of(admin));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(admin)).thenReturn(Optional.of(userLogin));

        // Simula falha ao salvar UserLogin
        doThrow(new RuntimeException("Erro ao salvar UserLogin")).when(userLoginRepository).save(userLogin);

        Exception exception = assertThrows(RuntimeException.class, () ->
                administradorService.desativarAdministrador(id)
        );

        assertEquals("Erro ao salvar UserLogin", exception.getMessage());

        // Confirma que a tentativa de salvar UserLogin ocorreu antes da falha
        verify(userLoginRepository, times(1)).save(userLogin);
        // Verifica que o Administrador não foi salvo após o erro
        verify(administradorRepository, never()).save(admin);
    }

    @Test
    public void testDesativarAdministrador_UserLoginAlreadyInactive() {
        Long id = 1L;
        Administrador admin = new Administrador();
        admin.setId(id);

        UserLogin userLogin = new UserLogin();
        userLogin.setAtivo(false); // Login já inativo

        when(administradorRepository.findByIdAndAtivoTrue(id)).thenReturn(Optional.of(admin));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(admin)).thenReturn(Optional.of(userLogin));

        administradorService.desativarAdministrador(id);

        // Verifica que o Administrador foi desativado
        assertFalse(admin.isAtivo());

        // Confirma que o UserLogin NÃO foi atualizado pois já estava inativo
        verify(userLoginRepository, never()).save(userLogin);
        verify(administradorRepository, times(1)).save(admin);
    }



}
