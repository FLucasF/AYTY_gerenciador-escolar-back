package br.com.ufpb.GerenciadorEscolar.service.administrador;

import br.com.ufpb.GerenciadorEscolar.model.Administrador;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdministradorServiceImplFindByEmailTest extends BaseAdministradorServiceTest {

    // ✅ Teste para caso de sucesso (administrador encontrado)
    @Test
    public void testFindByEmail_Found() {
        String email = "email@teste.com";
        Administrador admin = new Administrador();

        when(administradorRepository.findByEmailAndAtivoTrue(email)).thenReturn(Optional.of(admin));

        Optional<Administrador> result = administradorService.findByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(admin, result.get()); // Verifica se o objeto retornado é o esperado
        verify(administradorRepository, times(1)).findByEmailAndAtivoTrue(email);
    }

    // ✅ Teste para quando o administrador não for encontrado (retorna Optional.empty)
    @Test
    public void testFindByEmail_NotFound() {
        String email = "email@teste.com";

        when(administradorRepository.findByEmailAndAtivoTrue(email)).thenReturn(Optional.empty());

        Optional<Administrador> result = administradorService.findByEmail(email);

        assertFalse(result.isPresent());
        verify(administradorRepository, times(1)).findByEmailAndAtivoTrue(email);
    }

    // ✅ Teste para quando o email for `null` (deve lançar exceção)
    @Test
    public void testFindByEmail_NullEmail() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                administradorService.findByEmail(null)
        );

        assertEquals("Email não pode ser nulo ou vazio", exception.getMessage());
        verify(administradorRepository, never()).findByEmailAndAtivoTrue(any());
    }

    // ✅ Teste para quando o email for vazio (deve lançar exceção)
    @Test
    public void testFindByEmail_EmptyEmail() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                administradorService.findByEmail("")
        );

        assertEquals("Email não pode ser nulo ou vazio", exception.getMessage());
        verify(administradorRepository, never()).findByEmailAndAtivoTrue(any());
    }
}
