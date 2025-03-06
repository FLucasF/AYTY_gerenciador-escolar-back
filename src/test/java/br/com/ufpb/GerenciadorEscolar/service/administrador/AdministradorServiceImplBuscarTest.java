package br.com.ufpb.GerenciadorEscolar.service.administrador;

import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorResponse;
import br.com.ufpb.GerenciadorEscolar.model.Administrador;
import br.com.ufpb.GerenciadorEscolar.service.AdministradorNaoEncontradoException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdministradorServiceImplBuscarTest extends BaseAdministradorServiceTest {

    @Test
    void deveRetornarAdministrador_QuandoIdExistir() {
        // Arrange - Criando um administrador existente no banco
        Administrador admin = criarAdministradorPadrao();
        AdministradorResponse responseEsperado = mock(AdministradorResponse.class);

        when(administradorRepository.findByIdAndAtivoTrue(admin.getId())).thenReturn(Optional.of(admin));
        when(administradorMapper.toResponse(admin)).thenReturn(responseEsperado);

        // Act
        AdministradorResponse response = administradorService.buscarAdministradorPorId(admin.getId());

        // Assert
        assertNotNull(response);
        assertEquals(responseEsperado, response);

        verify(administradorRepository, times(1)).findByIdAndAtivoTrue(admin.getId());
        verify(administradorMapper, times(1)).toResponse(admin);
    }

    @Test
    void deveLancarExcecao_SeAdministradorNaoForEncontrado() {
        // Arrange
        Long idInexistente = 99L;
        when(administradorRepository.findByIdAndAtivoTrue(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        AdministradorNaoEncontradoException exception = assertThrows(
                AdministradorNaoEncontradoException.class,
                () -> administradorService.buscarAdministradorPorId(idInexistente)
        );

        assertEquals("Administrador não encontrado.", exception.getMessage());

        verify(administradorRepository, times(1)).findByIdAndAtivoTrue(idInexistente);
        verify(administradorMapper, never()).toResponse(any());
    }
}
