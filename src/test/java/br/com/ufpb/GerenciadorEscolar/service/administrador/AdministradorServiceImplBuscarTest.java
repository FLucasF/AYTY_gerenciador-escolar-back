package br.com.ufpb.GerenciadorEscolar.service.administrador;
import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorResponse;
import br.com.ufpb.GerenciadorEscolar.model.Administrador;
import org.junit.jupiter.api.Test;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdministradorServiceImplBuscarTest extends BaseAdministradorServiceTest {

    // ✅ Teste: Administrador encontrado com sucesso
    @Test
    public void testBuscarAdministradorPorId_Found() {
        Long id = 1L;
        Administrador admin = new Administrador();
        admin.setId(id);

        AdministradorResponse response = new AdministradorResponse(
                admin.getId(), "Nome", "email@teste.com", "cpf123", "setor", "1234567"
        );

        when(administradorRepository.findByIdAndAtivoTrue(id)).thenReturn(Optional.of(admin));
        when(administradorMapper.toResponse(admin)).thenReturn(response);

        Optional<AdministradorResponse> result = administradorService.buscarAdministradorPorId(id);

        assertTrue(result.isPresent());
        assertEquals(response, result.get());
        verify(administradorRepository, times(1)).findByIdAndAtivoTrue(id);
    }

    // ✅ Teste: Administrador não encontrado (retorna Optional.empty)
    @Test
    public void testBuscarAdministradorPorId_NotFound() {
        Long id = 1L;

        when(administradorRepository.findByIdAndAtivoTrue(id)).thenReturn(Optional.empty());

        Optional<AdministradorResponse> result = administradorService.buscarAdministradorPorId(id);

        assertFalse(result.isPresent());
        verify(administradorRepository, times(1)).findByIdAndAtivoTrue(id);
    }

    // ❌ Teste: ID nulo deve lançar exceção
    @Test
    public void testBuscarAdministradorPorId_NullId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                administradorService.buscarAdministradorPorId(null)
        );

        assertEquals("ID não pode ser nulo ou inválido", exception.getMessage());
        verify(administradorRepository, never()).findByIdAndAtivoTrue(any());
    }

    // ❌ Teste: ID negativo deve lançar exceção
    @Test
    public void testBuscarAdministradorPorId_NegativeId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                administradorService.buscarAdministradorPorId(-1L)
        );

        assertEquals("ID não pode ser nulo ou inválido", exception.getMessage());
        verify(administradorRepository, never()).findByIdAndAtivoTrue(any());
    }
}
