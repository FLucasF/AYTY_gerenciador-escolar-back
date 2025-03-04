package br.com.ufpb.GerenciadorEscolar.service.administrador;

import br.com.ufpb.GerenciadorEscolar.model.Administrador;
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
        admin.setAtivo(true);

        // Simula que existe um administrador ativo com o ID informado
        when(administradorRepository.findByIdAndAtivoTrue(id)).thenReturn(Optional.of(admin));

        // Executa o método de desativação
        administradorService.desativarAdministrador(id);

        // Verifica se o administrador foi desativado e se o método save foi chamado
        assertFalse(admin.isAtivo());
        verify(administradorRepository, times(1)).save(admin);
    }

    @Test
    public void testDesativarAdministrador_NotFound() {
        Long id = 1L;

        // Simula que não existe nenhum administrador ativo com o ID informado
        when(administradorRepository.findByIdAndAtivoTrue(id)).thenReturn(Optional.empty());

        // Verifica se o método lança a exceção com a mensagem esperada
        Exception exception = assertThrows(RuntimeException.class, () ->
                administradorService.desativarAdministrador(id)
        );
        assertEquals("Administrador não encontrado", exception.getMessage());
    }

    @Test
    public void testDesativarAdministrador_AlreadyInactive() {
        Long id = 1L;
        Administrador admin = new Administrador();
        admin.setId(id);
        admin.setAtivo(false); // Administrador já está inativo!

        // Simula que o administrador já está desativado
        when(administradorRepository.findByIdAndAtivoTrue(id)).thenReturn(Optional.empty());

        // Deve lançar a exceção pois o administrador já está inativo
        Exception exception = assertThrows(RuntimeException.class, () ->
                administradorService.desativarAdministrador(id)
        );
        assertEquals("Administrador não encontrado", exception.getMessage());

        // Verifica que o método save NÃO foi chamado, pois não deveria tentar desativar de novo
        verify(administradorRepository, never()).save(any(Administrador.class));
    }
}
