package br.com.ufpb.GerenciadorEscolar.service.administrador;

import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorRequest;
import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorResponse;
import br.com.ufpb.GerenciadorEscolar.model.Administrador;
import br.com.ufpb.GerenciadorEscolar.model.UserLogin;
import br.com.ufpb.GerenciadorEscolar.service.NenhumaAlteracaoRealizadaException;
import br.com.ufpb.GerenciadorEscolar.service.AdministradorNaoEncontradoException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdministradorServiceImplAtualizarTest extends BaseAdministradorServiceTest {

    @Test
    void deveAtualizarAdministradorEUserLoginComSucesso() {
        // Arrange
        Administrador admin = criarAdministradorPadrao();
        UserLogin userLogin = criarUserLoginPadrao(admin);

        AdministradorRequest request = new AdministradorRequest(
                "João Souza", "novo@email.com", "NovaSenha@123",
                admin.getCpf(), "TI", admin.getSiape()
        );

        when(administradorRepository.findByIdAndAtivoTrue(admin.getId())).thenReturn(Optional.of(admin));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(admin)).thenReturn(Optional.of(userLogin));
        when(passwordEncoder.encode("NovaSenha@123")).thenReturn("NovaSenhaCriptografada");
        when(administradorMapper.toResponse(any())).thenReturn(mock(AdministradorResponse.class));

        // Act
        AdministradorResponse response = administradorService.atualizarAdministrador(admin.getId(), request);

        // Assert
        assertNotNull(response);
        assertEquals("João Souza", admin.getNome());
        assertEquals("TI", admin.getSetor());
        assertEquals("novo@email.com", admin.getEmail());
        assertEquals("novo@email.com", userLogin.getEmail());
        assertEquals("NovaSenhaCriptografada", admin.getSenha());
        assertEquals("NovaSenhaCriptografada", userLogin.getSenha());

        // Captura os objetos salvos
        ArgumentCaptor<Administrador> adminCaptor = ArgumentCaptor.forClass(Administrador.class);
        ArgumentCaptor<UserLogin> userLoginCaptor = ArgumentCaptor.forClass(UserLogin.class);

        verify(administradorRepository).save(adminCaptor.capture());
        verify(userLoginRepository).save(userLoginCaptor.capture());

        Administrador adminSalvo = adminCaptor.getValue();
        UserLogin userLoginSalvo = userLoginCaptor.getValue();

        // ✅ Validações do Administrador
        assertEquals("João Souza", adminSalvo.getNome());
        assertEquals("TI", adminSalvo.getSetor());
        assertEquals("novo@email.com", adminSalvo.getEmail());
        assertEquals("NovaSenhaCriptografada", adminSalvo.getSenha());

        // ✅ Validações do UserLogin
        assertEquals("novo@email.com", userLoginSalvo.getEmail());
        assertEquals("NovaSenhaCriptografada", userLoginSalvo.getSenha());
        assertEquals(adminSalvo, userLoginSalvo.getUsuario());
    }

    @Test
    void deveLancarExcecao_SeNenhumaAlteracaoForFeita() {
        // Arrange
        Administrador admin = criarAdministradorPadrao();
        UserLogin userLogin = criarUserLoginPadrao(admin);
        AdministradorRequest request = criarAdministradorRequestPadrao();

        when(administradorRepository.findByIdAndAtivoTrue(admin.getId())).thenReturn(Optional.of(admin));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(admin)).thenReturn(Optional.of(userLogin));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(NenhumaAlteracaoRealizadaException.class,
                () -> administradorService.atualizarAdministrador(admin.getId(), request));

        verify(administradorRepository, never()).save(any());
        verify(userLoginRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecao_SeAdministradorNaoForEncontrado() {
        when(administradorRepository.findByIdAndAtivoTrue(2L)).thenReturn(Optional.empty());

        assertThrows(AdministradorNaoEncontradoException.class,
                () -> administradorService.atualizarAdministrador(2L, criarAdministradorRequestPadrao()));
    }

    @Test
    void deveLancarExcecao_SeUserLoginNaoForEncontrado() {
        Administrador admin = criarAdministradorPadrao();

        when(administradorRepository.findByIdAndAtivoTrue(admin.getId())).thenReturn(Optional.of(admin));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(admin)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> administradorService.atualizarAdministrador(admin.getId(), criarAdministradorRequestPadrao()));
    }

    @Test
    void deveLancarExcecao_SeSenhaInformadaForIgual() {
        Administrador admin = criarAdministradorPadrao();
        UserLogin userLogin = criarUserLoginPadrao(admin);

        AdministradorRequest request = new AdministradorRequest(
                admin.getNome(), admin.getEmail(), "Senha@123",
                admin.getCpf(), admin.getSetor(), admin.getSiape()
        );

        when(administradorRepository.findByIdAndAtivoTrue(admin.getId())).thenReturn(Optional.of(admin));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(admin)).thenReturn(Optional.of(userLogin));
        when(passwordEncoder.matches("Senha@123", admin.getSenha())).thenReturn(true);

        assertThrows(NenhumaAlteracaoRealizadaException.class,
                () -> administradorService.atualizarAdministrador(admin.getId(), request));

        verify(administradorRepository, never()).save(any());
        verify(userLoginRepository, never()).save(any());
    }

    @Test
    void deveAtualizarSenha_SeInformadaDiferente() {
        Administrador admin = criarAdministradorPadrao();
        UserLogin userLogin = criarUserLoginPadrao(admin);

        AdministradorRequest request = new AdministradorRequest(
                admin.getNome(), admin.getEmail(), "NovaSenha@123",
                admin.getCpf(), admin.getSetor(), admin.getSiape()
        );

        when(administradorRepository.findByIdAndAtivoTrue(admin.getId())).thenReturn(Optional.of(admin));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(admin)).thenReturn(Optional.of(userLogin));
        when(passwordEncoder.encode("NovaSenha@123")).thenReturn("NovaSenhaCriptografada");

        administradorService.atualizarAdministrador(admin.getId(), request);

        assertEquals("NovaSenhaCriptografada", admin.getSenha());
        assertEquals("NovaSenhaCriptografada", userLogin.getSenha());

        verify(administradorRepository).save(admin);
        verify(userLoginRepository).save(userLogin);
    }

    @Test
    void deveAtualizarEmail_SeInformadoDiferente() {
        Administrador admin = criarAdministradorPadrao();
        UserLogin userLogin = criarUserLoginPadrao(admin);

        AdministradorRequest request = new AdministradorRequest(
                admin.getNome(), "novo@email.com", null,
                admin.getCpf(), admin.getSetor(), admin.getSiape()
        );

        when(administradorRepository.findByIdAndAtivoTrue(admin.getId())).thenReturn(Optional.of(admin));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(admin)).thenReturn(Optional.of(userLogin));

        administradorService.atualizarAdministrador(admin.getId(), request);

        assertEquals("novo@email.com", admin.getEmail());
        assertEquals("novo@email.com", userLogin.getEmail());

        verify(administradorRepository).save(admin);
        verify(userLoginRepository).save(userLogin);
    }
}
