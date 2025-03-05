package br.com.ufpb.GerenciadorEscolar.service.administrador;

import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorRequest;
import br.com.ufpb.GerenciadorEscolar.model.Administrador;
import br.com.ufpb.GerenciadorEscolar.model.UserLogin;
import br.com.ufpb.GerenciadorEscolar.service.NenhumaAlteracaoRealizadaException;
import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorResponse;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdministradorServiceImplAtualizarTest extends BaseAdministradorServiceTest {

    @Test
    void deveAtualizarAdministradorComSucesso() {
        // Criando um administrador existente no banco
        Administrador admin = new Administrador();
        admin.setId(1L);
        admin.setNome("João Silva");
        admin.setEmail("joao@email.com");
        admin.setCpf("12345678901");
        admin.setSetor("RH");
        admin.setSiape("1234567");
        admin.setSenha("senhaAntiga");

        UserLogin userLogin = new UserLogin();
        userLogin.setUsuario(admin);
        userLogin.setEmail("joao@email.com");
        userLogin.setSenha("senhaAntiga");

        // Criando uma requisição com dados alterados
        AdministradorRequest request = new AdministradorRequest(
                "João Souza", "joao@email.com", null,
                "12345678901", "TI", "1234567"
        );

        when(administradorRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(admin));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(admin)).thenReturn(Optional.of(userLogin));
        when(administradorMapper.toResponse(any())).thenReturn(mock(AdministradorResponse.class));

        AdministradorResponse response = administradorService.atualizarAdministrador(1L, request);

        assertNotNull(response);
        assertEquals("João Souza", admin.getNome());
        assertEquals("TI", admin.getSetor());

        verify(administradorRepository).save(admin);
        verify(userLoginRepository).save(userLogin);
    }

    @Test
    void deveLancarExcecaoQuandoNenhumaAlteracaoForFeita() {
        Administrador admin = new Administrador();
        admin.setId(1L);
        admin.setNome("João Silva");
        admin.setEmail("joao@email.com");
        admin.setCpf("12345678901");
        admin.setSetor("RH");
        admin.setSiape("1234567");

        AdministradorRequest request = new AdministradorRequest(
                "João Silva", "joao@email.com", null,
                "12345678901", "RH", "1234567"
        );

        when(administradorRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(admin));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(admin)).thenReturn(Optional.of(new UserLogin()));

        assertThrows(NenhumaAlteracaoRealizadaException.class,
                () -> administradorService.atualizarAdministrador(1L, request));
    }

    @Test
    void deveLancarExcecaoQuandoAdministradorNaoEncontrado() {
        when(administradorRepository.findByIdAndAtivoTrue(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> administradorService.atualizarAdministrador(2L, new AdministradorRequest(
                        "Novo Nome", "novo@email.com", null, "12345678901", "TI", "7654321"
                )));
    }

    @Test
    void deveLancarExcecaoQuandoUserLoginNaoEncontrado() {
        Administrador admin = new Administrador();
        admin.setId(1L);
        admin.setNome("João Silva");

        when(administradorRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(admin));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(admin)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> administradorService.atualizarAdministrador(1L, new AdministradorRequest(
                        "Novo Nome", "novo@email.com", null, "12345678901", "TI", "7654321"
                )));
    }

//    @Test
//    void deveManterSenhaQuandoInformadaIgual() {
//        // 🔥 Criando um administrador e login com senha já definida
//        Administrador administrador = criarAdministradorPadrao();
//        UserLogin userLogin = criarUserLoginPadrao(administrador);
//
//        // Criando um request onde a senha informada é a mesma
//        AdministradorRequest request = criarAdministradorRequest(
//                administrador.getNome(), administrador.getEmail(), "senhaAntiga",
//                administrador.getCpf()
//        );
//
//        when(administradorRepository.findByIdAndAtivoTrue(administrador.getId())).thenReturn(Optional.of(administrador));
//        when(userLoginRepository.findByUsuarioAndAtivoTrue(administrador)).thenReturn(Optional.of(userLogin));
//        when(passwordEncoder.matches("senhaAntiga", administrador.getSenha())).thenReturn(true); // 🔥 Verificação correta
//
//        // 🔥 Testando se a exceção correta é lançada
//        assertThrows(NenhumaAlteracaoRealizadaException.class,
//                () -> administradorService.atualizarAdministrador(administrador.getId(), request));
//
//        // 🔥 Garantindo que o administrador NÃO FOI salvo, pois nada mudou
//        verify(administradorRepository, never()).save(any());
//        verify(userLoginRepository, never()).save(any());
//    }


//    @Test
//    void deveManterSenhaQuandoInformadaIgual() {
//        // 🔹 Criando um administrador existente no "banco"
//        Administrador admin = new Administrador();
//        admin.setId(1L);
//        admin.setNome("João Silva");
//        admin.setEmail("joao@email.com");
//        admin.setCpf("12345678901");
//        admin.setSetor("RH");
//        admin.setSiape("1234567");
//        admin.setSenha("senhaAntiga"); // Senha armazenada no banco
//
//        UserLogin userLogin = new UserLogin();
//        userLogin.setUsuario(admin);
//        userLogin.setEmail("joao@email.com");
//        userLogin.setSenha("senhaAntiga"); // Senha armazenada no banco
//
//        // 🔹 Criando um request idêntico ao banco (nenhuma alteração)
//        AdministradorRequest request = new AdministradorRequest(
//                "João Silva", "joao@email.com", "senhaAntiga",
//                "12345678901", "RH", "1234567"
//        );
//
//        // 🔹 Simulando comportamento dos repositórios e password encoder
//        when(administradorRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(admin));
//        when(userLoginRepository.findByUsuarioAndAtivoTrue(admin)).thenReturn(Optional.of(userLogin));
//        when(passwordEncoder.encode("senhaAntiga")).thenReturn("senhaAntiga"); // Senha igual
//
//        // ✅ Verifica se a exceção é lançada corretamente quando nada muda
//        assertThrows(NenhumaAlteracaoRealizadaException.class,
//                () -> administradorService.atualizarAdministrador(1L, request));
//
//        // ✅ Verifica que a senha foi processada apenas uma vez
//        verify(passwordEncoder, times(1)).encode("senhaAntiga");
//
//        // ✅ Garante que o banco **não** foi atualizado, pois nada mudou
//        verify(administradorRepository, never()).save(any());
//        verify(userLoginRepository, never()).save(any());
//    }

}
