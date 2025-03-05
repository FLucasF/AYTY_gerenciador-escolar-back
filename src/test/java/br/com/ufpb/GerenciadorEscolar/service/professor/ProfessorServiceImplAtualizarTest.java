package br.com.ufpb.GerenciadorEscolar.service.professor;

import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorRequest;
import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorResponse;
import br.com.ufpb.GerenciadorEscolar.model.Professor;
import br.com.ufpb.GerenciadorEscolar.model.UserLogin;
import br.com.ufpb.GerenciadorEscolar.service.NenhumaAlteracaoRealizadaException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfessorServiceImplAtualizarTest extends BaseProfessorServiceTest {

    @Test
    void deveAtualizarProfessorComSucesso() {
        Professor professor = criarProfessorPadrao();
        UserLogin userLogin = criarUserLoginPadrao(professor);

        ProfessorRequest request = criarProfessorRequest(
                "Carlos Silva", professor.getEmail(), null,
                professor.getCpf(), "Matemática", professor.getSiape()
        );

        when(professorRepository.findByIdAndAtivoTrue(professor.getId())).thenReturn(Optional.of(professor));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(professor)).thenReturn(Optional.of(userLogin));
        when(professorMapper.toResponse(any())).thenReturn(mock(ProfessorResponse.class));

        ProfessorResponse response = professorService.atualizarProfessor(professor.getId(), request);

        assertNotNull(response);
        assertEquals("Carlos Silva", professor.getNome());
        assertEquals("Matemática", professor.getDepartamento());

        verify(professorRepository).save(professor);
        verify(userLoginRepository).save(userLogin);
    }

    @Test
    void deveAtualizarTodosOsCamposDoProfessor() {
        // Criar um professor existente
        Professor professor = criarProfessorPadrao();
        UserLogin userLogin = criarUserLoginPadrao(professor);

        // Criar um request com TODOS os campos alterados
        ProfessorRequest request = criarProfessorRequest(
                "Carlos Oliveira",      // Nome atualizado
                "carlosnovo@email.com", // Email atualizado
                "novaSenha123",         // Senha atualizada
                "11122233344",          // CPF atualizado
                "Física",               // Departamento atualizado
                "7654321"               // SIAPE atualizado
        );

        when(professorRepository.findByIdAndAtivoTrue(professor.getId())).thenReturn(Optional.of(professor));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(professor)).thenReturn(Optional.of(userLogin));
        when(passwordEncoder.encode("novaSenha123")).thenReturn("senhaCriptografada");
        when(professorMapper.toResponse(any())).thenReturn(mock(ProfessorResponse.class));

        // Executa a atualização
        ProfessorResponse response = professorService.atualizarProfessor(professor.getId(), request);

        // Verifica se os campos foram atualizados corretamente
        assertNotNull(response);
        assertEquals("Carlos Oliveira", professor.getNome());
        assertEquals("carlosnovo@email.com", professor.getEmail());
        assertEquals("11122233344", professor.getCpf());
        assertEquals("Física", professor.getDepartamento());
        assertEquals("7654321", professor.getSiape());
        assertEquals("senhaCriptografada", professor.getSenha());
        assertEquals("senhaCriptografada", userLogin.getSenha());
        assertEquals("carlosnovo@email.com", userLogin.getEmail());

        // Verifica chamadas nos repositórios
        verify(professorRepository).save(professor);
        verify(userLoginRepository).save(userLogin);
    }

    @Test
    void deveLancarExcecaoQuandoNenhumaAlteracaoForFeita() {
        Professor professor = criarProfessorPadrao();

        ProfessorRequest request = criarProfessorRequest(
                professor.getNome(), professor.getEmail(), null,
                professor.getCpf(), professor.getDepartamento(), professor.getSiape()
        );

        when(professorRepository.findByIdAndAtivoTrue(professor.getId())).thenReturn(Optional.of(professor));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(professor)).thenReturn(Optional.of(new UserLogin()));

        assertThrows(NenhumaAlteracaoRealizadaException.class,
                () -> professorService.atualizarProfessor(professor.getId(), request));
    }

    @Test
    void deveLancarExcecaoQuandoProfessorNaoEncontrado() {
        when(professorRepository.findByIdAndAtivoTrue(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> professorService.atualizarProfessor(2L, criarProfessorRequest(
                        "Novo Nome", "novo@email.com", null, "12345678901", "Física", "7654321"
                )));
    }

    @Test
    void deveLancarExcecaoQuandoUserLoginNaoEncontrado() {
        Professor professor = criarProfessorPadrao();

        when(professorRepository.findByIdAndAtivoTrue(professor.getId())).thenReturn(Optional.of(professor));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(professor)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> professorService.atualizarProfessor(professor.getId(), criarProfessorRequest(
                        "Novo Nome", "novo@email.com", null, "12345678901", "Física", "7654321"
                )));
    }

    @Test
    void deveManterSenhaQuandoInformadaIgual() {
        // 🔥 Criando um professor e login com senha já definida
        Professor professor = criarProfessorPadrao();
        UserLogin userLogin = criarUserLoginPadrao(professor);

        // Criando um request onde a senha informada é a mesma
        ProfessorRequest request = criarProfessorRequest(
                professor.getNome(), professor.getEmail(), "senhaAntiga",
                professor.getCpf(), professor.getDepartamento(), professor.getSiape()
        );

        when(professorRepository.findByIdAndAtivoTrue(professor.getId())).thenReturn(Optional.of(professor));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(professor)).thenReturn(Optional.of(userLogin));
        when(passwordEncoder.matches("senhaAntiga", professor.getSenha())).thenReturn(true); // 🔥 Verificação correta

        // 🔥 Testando se a exceção correta é lançada
        assertThrows(NenhumaAlteracaoRealizadaException.class,
                () -> professorService.atualizarProfessor(professor.getId(), request));

        // 🔥 Garantindo que o professor NÃO FOI salvo, pois nada mudou
        verify(professorRepository, never()).save(any());
        verify(userLoginRepository, never()).save(any());
    }



    @Test
    void deveLancarExcecaoQuandoEmailJaExistente() {
        Professor professor = criarProfessorPadrao();
        Professor outroProfessor = new Professor();
        outroProfessor.setId(2L);
        outroProfessor.setEmail(professor.getEmail());

        ProfessorRequest request = criarProfessorRequest(
                professor.getNome(), professor.getEmail(), null,
                professor.getCpf(), professor.getDepartamento(), professor.getSiape()
        );

        when(professorRepository.findByIdAndAtivoTrue(professor.getId())).thenReturn(Optional.of(professor));
        when(professorRepository.findByEmailAndAtivoTrue(professor.getEmail())).thenReturn(Optional.of(outroProfessor));

        assertThrows(RuntimeException.class,
                () -> professorService.atualizarProfessor(professor.getId(), request));
    }

    @Test
    void deveAtualizarSenhaQuandoInformadaNova() {
        Professor professor = criarProfessorPadrao();
        UserLogin userLogin = criarUserLoginPadrao(professor);

        ProfessorRequest request = criarProfessorRequest(
                professor.getNome(), professor.getEmail(), "novaSenha",
                professor.getCpf(), professor.getDepartamento(), professor.getSiape()
        );

        when(professorRepository.findByIdAndAtivoTrue(professor.getId())).thenReturn(Optional.of(professor));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(professor)).thenReturn(Optional.of(userLogin));
        when(passwordEncoder.encode("novaSenha")).thenReturn("senhaCriptografada");

        professorService.atualizarProfessor(professor.getId(), request);

        assertEquals("senhaCriptografada", professor.getSenha());
        assertEquals("senhaCriptografada", userLogin.getSenha());

        verify(professorRepository).save(professor);
        verify(userLoginRepository).save(userLogin);
    }

    @Test
    void deveAtualizarNomeDoProfessor() {
        Professor professor = criarProfessorPadrao();
        UserLogin userLogin = criarUserLoginPadrao(professor);

        ProfessorRequest request = criarProfessorRequest(
                "Carlos Oliveira", professor.getEmail(), null,
                professor.getCpf(), professor.getDepartamento(), professor.getSiape()
        );

        when(professorRepository.findByIdAndAtivoTrue(professor.getId())).thenReturn(Optional.of(professor));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(professor)).thenReturn(Optional.of(userLogin));

        professorService.atualizarProfessor(professor.getId(), request);

        assertEquals("Carlos Oliveira", professor.getNome());
        verify(professorRepository).save(professor);
    }

    @Test
    void deveAtualizarEmailDoProfessor() {
        Professor professor = criarProfessorPadrao();
        UserLogin userLogin = criarUserLoginPadrao(professor);

        ProfessorRequest request = criarProfessorRequest(
                professor.getNome(), "carlosnovo@email.com", null,
                professor.getCpf(), professor.getDepartamento(), professor.getSiape()
        );

        when(professorRepository.findByIdAndAtivoTrue(professor.getId())).thenReturn(Optional.of(professor));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(professor)).thenReturn(Optional.of(userLogin));

        professorService.atualizarProfessor(professor.getId(), request);

        assertEquals("carlosnovo@email.com", professor.getEmail());
        assertEquals("carlosnovo@email.com", userLogin.getEmail());
        verify(professorRepository).save(professor);
        verify(userLoginRepository).save(userLogin);
    }

    @Test
    void deveAtualizarSenhaDoProfessor() {
        Professor professor = criarProfessorPadrao();
        UserLogin userLogin = criarUserLoginPadrao(professor);

        ProfessorRequest request = criarProfessorRequest(
                professor.getNome(), professor.getEmail(), "novaSenha123",
                professor.getCpf(), professor.getDepartamento(), professor.getSiape()
        );

        when(professorRepository.findByIdAndAtivoTrue(professor.getId())).thenReturn(Optional.of(professor));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(professor)).thenReturn(Optional.of(userLogin));
        when(passwordEncoder.encode("novaSenha123")).thenReturn("senhaCriptografada");

        professorService.atualizarProfessor(professor.getId(), request);

        assertEquals("senhaCriptografada", professor.getSenha());
        assertEquals("senhaCriptografada", userLogin.getSenha());
        verify(professorRepository).save(professor);
        verify(userLoginRepository).save(userLogin);
    }

    @Test
    void deveAtualizarCpfDoProfessor() {
        Professor professor = criarProfessorPadrao();
        UserLogin userLogin = criarUserLoginPadrao(professor); // 🔥 Criando UserLogin corretamente

        ProfessorRequest request = criarProfessorRequest(
                professor.getNome(), professor.getEmail(), null,
                "11122233344", professor.getDepartamento(), professor.getSiape()
        );

        when(professorRepository.findByIdAndAtivoTrue(professor.getId())).thenReturn(Optional.of(professor));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(professor)).thenReturn(Optional.of(userLogin)); // 🔥 Mockando UserLogin

        professorService.atualizarProfessor(professor.getId(), request);

        assertEquals("11122233344", professor.getCpf());
        verify(professorRepository).save(professor);
    }


    @Test
    void deveAtualizarDepartamentoDoProfessor() {
        Professor professor = criarProfessorPadrao();
        UserLogin userLogin = criarUserLoginPadrao(professor); // 🔥 Criando o UserLogin corretamente

        ProfessorRequest request = criarProfessorRequest(
                professor.getNome(), professor.getEmail(), null,
                professor.getCpf(), "Física", professor.getSiape()
        );

        when(professorRepository.findByIdAndAtivoTrue(professor.getId())).thenReturn(Optional.of(professor));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(professor)).thenReturn(Optional.of(userLogin)); // 🔥 Mockando UserLogin

        professorService.atualizarProfessor(professor.getId(), request);

        assertEquals("Física", professor.getDepartamento());
        verify(professorRepository).save(professor);
    }

    @Test
    void deveAtualizarSiapeDoProfessor() {
        Professor professor = criarProfessorPadrao();
        UserLogin userLogin = criarUserLoginPadrao(professor); // 🔥 Criando UserLogin corretamente

        ProfessorRequest request = criarProfessorRequest(
                professor.getNome(), professor.getEmail(), null,
                professor.getCpf(), professor.getDepartamento(), "7654321"
        );

        when(professorRepository.findByIdAndAtivoTrue(professor.getId())).thenReturn(Optional.of(professor));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(professor)).thenReturn(Optional.of(userLogin)); // 🔥 Mockando UserLogin

        professorService.atualizarProfessor(professor.getId(), request);

        assertEquals("7654321", professor.getSiape());
        verify(professorRepository).save(professor);
    }

}
