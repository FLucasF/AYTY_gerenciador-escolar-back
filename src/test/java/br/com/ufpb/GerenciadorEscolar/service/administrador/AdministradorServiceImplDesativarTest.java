package br.com.ufpb.GerenciadorEscolar.service.administrador;

import br.com.ufpb.GerenciadorEscolar.model.entity.Administrador;
import br.com.ufpb.GerenciadorEscolar.model.entity.UserLogin;
import br.com.ufpb.GerenciadorEscolar.service.AdministradorNaoEncontradoException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdministradorServiceImplDesativarTest extends BaseAdministradorServiceTest {

    @Test
    void deveDesativarAdministradorEUserLoginComSucesso() {
        // Arrange
        Administrador admin = criarAdministradorPadrao();
        UserLogin userLogin = criarUserLoginPadrao(admin);

        when(administradorRepository.findByIdAndAtivoTrue(admin.getId())).thenReturn(Optional.of(admin));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(admin)).thenReturn(Optional.of(userLogin));

        // Act
        administradorService.desativarAdministrador(admin.getId());

        // Captura os valores salvos
        ArgumentCaptor<Administrador> adminCaptor = ArgumentCaptor.forClass(Administrador.class);
        ArgumentCaptor<UserLogin> userLoginCaptor = ArgumentCaptor.forClass(UserLogin.class);

        verify(administradorRepository).save(adminCaptor.capture());
        verify(userLoginRepository).save(userLoginCaptor.capture());

        Administrador adminDesativado = adminCaptor.getValue();
        UserLogin userLoginDesativado = userLoginCaptor.getValue();

        // ✅ Validações
        assertFalse(adminDesativado.isAtivo(), "O administrador deve estar desativado.");
        assertFalse(userLoginDesativado.isAtivo(), "O login do administrador deve estar desativado.");

        verify(administradorRepository).save(admin);
        verify(userLoginRepository).save(userLogin);
    }

    @Test
    void deveDesativarSomenteAdministrador_SeNaoHouverUserLogin() {
        // Arrange
        Administrador admin = criarAdministradorPadrao();

        when(administradorRepository.findByIdAndAtivoTrue(admin.getId())).thenReturn(Optional.of(admin));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(admin)).thenReturn(Optional.empty());

        // Act
        administradorService.desativarAdministrador(admin.getId());

        // Captura os valores salvos
        ArgumentCaptor<Administrador> adminCaptor = ArgumentCaptor.forClass(Administrador.class);
        verify(administradorRepository).save(adminCaptor.capture());

        Administrador adminDesativado = adminCaptor.getValue();

        // ✅ Validações
        assertFalse(adminDesativado.isAtivo(), "O administrador deve estar desativado.");
        verify(userLoginRepository, never()).save(any()); // ✅ Não deve tentar desativar UserLogin inexistente
    }

    @Test
    void deveLancarExcecao_SeAdministradorNaoForEncontrado() {
        // Arrange
        when(administradorRepository.findByIdAndAtivoTrue(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AdministradorNaoEncontradoException.class,
                () -> administradorService.desativarAdministrador(999L));

        // ✅ Garante que nada foi salvo no banco
        verify(administradorRepository, never()).save(any());
        verify(userLoginRepository, never()).save(any());
    }
}
