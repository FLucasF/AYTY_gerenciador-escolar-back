package br.com.ufpb.GerenciadorEscolar.service.administrador;

import br.com.ufpb.GerenciadorEscolar.model.dto.administrador.AdministradorRequest;
import br.com.ufpb.GerenciadorEscolar.model.dto.administrador.AdministradorResponse;
import br.com.ufpb.GerenciadorEscolar.model.entity.Administrador;
import br.com.ufpb.GerenciadorEscolar.model.entity.UserLogin;
import br.com.ufpb.GerenciadorEscolar.service.CpfJaCadastradoException;
import br.com.ufpb.GerenciadorEscolar.service.EmailJaCadastradoException;
import br.com.ufpb.GerenciadorEscolar.service.SiapeJaCadastradoException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdministradorServiceImplCadastrarTest extends BaseAdministradorServiceTest {

    @Test
    void deveCadastrarAdministradorELoginComSucesso() {
        // Arrange
        AdministradorRequest request = criarAdministradorRequestPadrao();
        Administrador admin = criarAdministradorPadrao();

        when(administradorRepository.findByEmailAndAtivoTrue(request.email())).thenReturn(Optional.empty());
        when(administradorRepository.findByCpfAndAtivoTrue(request.cpf())).thenReturn(Optional.empty());
        when(administradorRepository.findBySiapeAndAtivoTrue(request.siape())).thenReturn(Optional.empty());
        when(administradorMapper.toEntity(request)).thenReturn(admin);
        when(passwordEncoder.encode(request.senha())).thenReturn("SenhaCriptografada");
        when(administradorMapper.toResponse(admin)).thenReturn(mock(AdministradorResponse.class));

        // Act
        AdministradorResponse response = administradorService.cadastrarAdministrador(request);

        // Assert
        assertNotNull(response);

        // 🔍 Capturando os objetos salvos
        ArgumentCaptor<Administrador> adminCaptor = ArgumentCaptor.forClass(Administrador.class);
        ArgumentCaptor<UserLogin> userLoginCaptor = ArgumentCaptor.forClass(UserLogin.class);

        verify(administradorRepository).save(adminCaptor.capture());
        verify(userLoginRepository).save(userLoginCaptor.capture());

        Administrador adminSalvo = adminCaptor.getValue();
        UserLogin userLoginSalvo = userLoginCaptor.getValue();

        // ✅ Verificações no Administrador salvo
        assertEquals("SenhaCriptografada", adminSalvo.getSenha(), "A senha do administrador deve estar criptografada.");
        assertEquals(request.nome(), adminSalvo.getNome());
        assertEquals(request.email(), adminSalvo.getEmail());
        assertEquals(request.cpf(), adminSalvo.getCpf());
        assertEquals(request.setor(), adminSalvo.getSetor());
        assertEquals(request.siape(), adminSalvo.getSiape());

        // ✅ Verificações no UserLogin salvo
        assertEquals(adminSalvo.getEmail(), userLoginSalvo.getEmail(), "O email do login deve ser igual ao do administrador.");
        assertEquals("SenhaCriptografada", userLoginSalvo.getSenha(), "A senha criptografada deve ser a mesma no UserLogin.");
        assertEquals(adminSalvo, userLoginSalvo.getUsuario(), "O login deve estar associado ao administrador criado.");
    }

    @Test
    void deveLancarExcecao_SeEmailJaCadastrado() {
        // Arrange
        AdministradorRequest request = criarAdministradorRequestPadrao();
        when(administradorRepository.findByEmailAndAtivoTrue(request.email())).thenReturn(Optional.of(criarAdministradorPadrao()));

        // Act & Assert
        assertThrows(EmailJaCadastradoException.class,
                () -> administradorService.cadastrarAdministrador(request));

        verify(administradorRepository, never()).save(any());
        verify(userLoginRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecao_SeCpfJaCadastrado() {
        // Arrange
        AdministradorRequest request = criarAdministradorRequestPadrao();
        when(administradorRepository.findByCpfAndAtivoTrue(request.cpf())).thenReturn(Optional.of(criarAdministradorPadrao()));

        // Act & Assert
        assertThrows(CpfJaCadastradoException.class,
                () -> administradorService.cadastrarAdministrador(request));

        verify(administradorRepository, never()).save(any());
        verify(userLoginRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecao_SeSiapeJaCadastrado() {
        // Arrange
        AdministradorRequest request = criarAdministradorRequestPadrao();
        when(administradorRepository.findBySiapeAndAtivoTrue(request.siape())).thenReturn(Optional.of(criarAdministradorPadrao()));

        // Act & Assert
        assertThrows(SiapeJaCadastradoException.class,
                () -> administradorService.cadastrarAdministrador(request));

        verify(administradorRepository, never()).save(any());
        verify(userLoginRepository, never()).save(any());
    }
}
