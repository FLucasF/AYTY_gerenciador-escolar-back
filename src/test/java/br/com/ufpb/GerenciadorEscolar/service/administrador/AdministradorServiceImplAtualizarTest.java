package br.com.ufpb.GerenciadorEscolar.service.administrador;

import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorRequest;
import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorResponse;
import br.com.ufpb.GerenciadorEscolar.model.Administrador;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdministradorServiceImplAtualizarTest extends BaseAdministradorServiceTest {

    // ✅ Teste de atualização bem-sucedida
    @Test
    public void testAtualizarAdministrador_Success() {

        Long id = 1L;
        AdministradorRequest request = new AdministradorRequest("Novo Nome", "novoemail@teste.com", "novocpf", "novasenha", "novo setor", "1234567");

        Administrador admin = new Administrador();
        admin.setId(id);
        admin.setAtivo(true);
        admin.setSenha("senhaAntiga");

        when(administradorRepository.findByIdAndAtivoTrue(id)).thenReturn(Optional.of(admin));
        when(administradorRepository.findByEmailAndAtivoTrue("novoemail@teste.com")).thenReturn(Optional.empty());
        when(administradorRepository.findByCpfAndAtivoTrue("novocpf")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("novasenha")).thenReturn("senhaNovaCodificada");
        when(administradorRepository.save(admin)).thenReturn(admin);

        AdministradorResponse response = new AdministradorResponse(
                id, "Novo Nome", "novoemail@teste.com", "novocpf", "novo setor", "1234567"
        );
        when(administradorMapper.toResponse(admin)).thenReturn(response);

        AdministradorResponse result = administradorService.atualizarAdministrador(id, request);

        assertNotNull(result);
        assertEquals("Novo Nome", result.nome());
        assertEquals("novoemail@teste.com", result.email());
        assertEquals("novocpf", result.cpf());
        assertEquals("novo setor", result.setor());

        verify(administradorRepository, times(1)).findByIdAndAtivoTrue(id);
        verify(administradorRepository, times(1)).save(admin);
    }

    // ✅ Teste: Administrador não encontrado
    @Test
    public void testAtualizarAdministrador_AdminNotFound() {
        Long id = 1L;
        AdministradorRequest request = new AdministradorRequest("Nome", "email@teste.com", "cpf123", "senha123", "setor", "1234567");

        when(administradorRepository.findByIdAndAtivoTrue(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                administradorService.atualizarAdministrador(id, request)
        );
        assertEquals("Administrador não encontrado ou inativo.", exception.getMessage());
    }

    // ❌ Teste: ID nulo deve lançar exceção
    @Test
    public void testAtualizarAdministrador_NullId() {
        AdministradorRequest request = new AdministradorRequest("Nome", "email@teste.com", "cpf123", "senha123", "setor", "1234567");

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                administradorService.atualizarAdministrador(null, request)
        );
        assertEquals("ID não pode ser nulo ou inválido", exception.getMessage());
    }

    // ✅ Teste: Senha não alterada (mantém a senha antiga)
    @Test
    public void testAtualizarAdministrador_KeepOldPassword() {
        Long id = 1L;
        AdministradorRequest request = new AdministradorRequest(
                "Nome", "email@teste.com", "", "", "setor", "1234567" // Senha vazia (deve manter a antiga)
        );

        Administrador admin = new Administrador();
        admin.setId(id);
        admin.setAtivo(true);
        admin.setSenha("senhaAntiga"); // Senha original

        when(administradorRepository.findByIdAndAtivoTrue(id)).thenReturn(Optional.of(admin));
        when(administradorRepository.save(admin)).thenReturn(admin);
        when(administradorMapper.toResponse(admin)).thenReturn(new AdministradorResponse(
                id, "Nome", "email@teste.com", "cpf123", "setor", "1234567"
        ));

        AdministradorResponse result = administradorService.atualizarAdministrador(id, request);

        assertNotNull(result);
        assertEquals("senhaAntiga", admin.getSenha()); // ✅ Senha não foi alterada
        verify(administradorRepository, times(1)).save(admin);
    }


    // ❌ Teste: Falha ao salvar no banco deve lançar exceção
    @Test
    public void testAtualizarAdministrador_SaveError() {
        Long id = 1L;
        AdministradorRequest request = new AdministradorRequest("Nome", "email@teste.com", "cpf123", "senha123", "setor", "1234567");

        Administrador admin = new Administrador();
        admin.setId(id);
        admin.setAtivo(true);

        when(administradorRepository.findByIdAndAtivoTrue(id)).thenReturn(Optional.of(admin));
        when(administradorRepository.save(any())).thenThrow(new RuntimeException("Erro ao atualizar administrador"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                administradorService.atualizarAdministrador(id, request)
        );

        assertEquals("Erro ao atualizar administrador", exception.getMessage());
    }
}
