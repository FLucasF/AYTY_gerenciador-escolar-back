package br.com.ufpb.GerenciadorEscolar.service.administrador;

import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorResponse;
import br.com.ufpb.GerenciadorEscolar.model.Administrador;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdministradorServiceImplListarTest extends BaseAdministradorServiceTest {

    // ✅ Testa listagem com administradores ativos
    @Test
    public void testListarAdministradoresAtivos_Success() {
        PageRequest pageable = PageRequest.of(0, 10);
        Administrador admin = new Administrador();
        admin.setId(1L);
        admin.setAtivo(true);

        AdministradorResponse response = new AdministradorResponse(
                admin.getId(), "Nome", "email@teste.com", "cpf123", "setor", "1234567"
        );
        when(administradorMapper.toResponse(admin)).thenReturn(response);

        Page<Administrador> adminPage = new PageImpl<>(Collections.singletonList(admin));
        when(administradorRepository.findAllByAtivoTrue(pageable)).thenReturn(adminPage);

        Page<AdministradorResponse> result = administradorService.listarAdministradoresAtivos(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(response, result.getContent().get(0));
        verify(administradorRepository, times(1)).findAllByAtivoTrue(pageable);
    }

    // ✅ Testa listagem quando não há administradores ativos
    @Test
    public void testListarAdministradoresAtivos_EmptyList() {
        PageRequest pageable = PageRequest.of(0, 10);

        Page<Administrador> emptyPage = new PageImpl<>(Collections.emptyList());
        when(administradorRepository.findAllByAtivoTrue(pageable)).thenReturn(emptyPage);

        Page<AdministradorResponse> result = administradorService.listarAdministradoresAtivos(pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
        verify(administradorRepository, times(1)).findAllByAtivoTrue(pageable);
    }

    // ✅ Testa erro no banco de dados
    @Test
    public void testListarAdministradoresAtivos_DatabaseFailure() {
        PageRequest pageable = PageRequest.of(0, 10);

        when(administradorRepository.findAllByAtivoTrue(pageable))
                .thenThrow(new RuntimeException("Erro ao acessar banco de dados"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                administradorService.listarAdministradoresAtivos(pageable)
        );

        assertEquals("Erro ao acessar banco de dados", exception.getMessage());
        verify(administradorRepository, times(1)).findAllByAtivoTrue(pageable);
    }


    // ❌ **Teste: Falha ao converter entidade para DTO** → Deve falhar
    @Test
    public void testListarAdministradoresAtivos_FailureOnMapping() {
        PageRequest pageable = PageRequest.of(0, 10);
        Administrador admin = new Administrador();
        admin.setId(1L);
        admin.setAtivo(true);

        Page<Administrador> adminPage = new PageImpl<>(Collections.singletonList(admin));
        when(administradorRepository.findAllByAtivoTrue(pageable)).thenReturn(adminPage);

        // Simula falha ao converter entidade para DTO
        when(administradorMapper.toResponse(admin))
                .thenThrow(new RuntimeException("Erro ao converter administrador para DTO"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                administradorService.listarAdministradoresAtivos(pageable)
        );

        assertEquals("Erro ao converter administrador para DTO", exception.getMessage());
    }
}
