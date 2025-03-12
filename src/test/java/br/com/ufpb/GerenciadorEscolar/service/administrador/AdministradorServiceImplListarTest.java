package br.com.ufpb.GerenciadorEscolar.service.administrador;

import br.com.ufpb.GerenciadorEscolar.model.dto.administrador.AdministradorResponse;
import br.com.ufpb.GerenciadorEscolar.model.entity.Administrador;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdministradorServiceImplListarTest extends BaseAdministradorServiceTest {

    @Test
    void deveRetornarPaginaDeAdministradoresAtivos() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Administrador admin1 = criarAdministradorPadrao();
        Administrador admin2 = new Administrador();
        admin2.setId(2L);
        admin2.setNome("Maria Oliveira");
        admin2.setEmail("maria@email.com");
        admin2.setCpf("98765432101");
        admin2.setSetor("Financeiro");
        admin2.setSiape("7654321");
        admin2.setSenha("Senha@123");

        List<Administrador> administradores = List.of(admin1, admin2);
        Page<Administrador> page = new PageImpl<>(administradores, pageable, administradores.size());

        when(administradorRepository.findAllByAtivoTrue(pageable)).thenReturn(page);
        when(administradorMapper.toResponse(admin1)).thenReturn(
                new AdministradorResponse(admin1.getId(), admin1.getNome(), admin1.getEmail(), admin1.getCpf(), admin1.getSetor(), admin1.getSiape())
        );
        when(administradorMapper.toResponse(admin2)).thenReturn(
                new AdministradorResponse(admin2.getId(), admin2.getNome(), admin2.getEmail(), admin2.getCpf(), admin2.getSetor(), admin2.getSiape())
        );

        // Act
        Page<AdministradorResponse> result = administradorService.listarAdministradoresAtivos(pageable);

        // Assert
        assertFalse(result.isEmpty(), "A página não deve estar vazia.");
        assertEquals(2, result.getTotalElements(), "Deve haver exatamente 2 administradores ativos.");
        assertEquals("João Silva", result.getContent().get(0).nome(), "O primeiro administrador deve ser João Silva.");
        assertEquals("Maria Oliveira", result.getContent().get(1).nome(), "O segundo administrador deve ser Maria Oliveira.");

        verify(administradorRepository).findAllByAtivoTrue(pageable);
        verify(administradorMapper, times(2)).toResponse(any(Administrador.class));
    }

    @Test
    void deveRetornarPaginaVazia_QuandoNaoHouverAdministradoresAtivos() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Administrador> pageVazia = Page.empty();

        when(administradorRepository.findAllByAtivoTrue(pageable)).thenReturn(pageVazia);

        // Act
        Page<AdministradorResponse> result = administradorService.listarAdministradoresAtivos(pageable);

        // Assert
        assertTrue(result.isEmpty(), "A página deve estar vazia.");

        verify(administradorRepository).findAllByAtivoTrue(pageable);
        verify(administradorMapper, never()).toResponse(any());
    }

}
