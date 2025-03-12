//package br.com.ufpb.GerenciadorEscolar.service.aluno;
//
//import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoRequest;
//import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoResponse;
//import br.com.ufpb.GerenciadorEscolar.model.Aluno;
//import br.com.ufpb.GerenciadorEscolar.model.UserLogin;
//import br.com.ufpb.GerenciadorEscolar.service.AlunoNaoEncontradoException;
//import br.com.ufpb.GerenciadorEscolar.service.NenhumaAlteracaoRealizadaException;
//import org.junit.jupiter.api.Test;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class AlunoServiceImplAtualizarTest extends BaseAlunoServiceTest {
//
//    @Test
//    void deveAtualizarNomeECurso_SeAlterados() {
//        // Arrange
//        Aluno aluno = criarAlunoPadrao();
//        UserLogin userLogin = criarUserLoginPadrao(aluno);
//
//        AlunoRequest request = new AlunoRequest(
//                "Novo Nome", aluno.getEmail(), null,
//                aluno.getCpf(), "Novo Curso"
//        );
//
//        when(alunoRepository.findByIdAndAtivoTrue(aluno.getId())).thenReturn(Optional.of(aluno));
//        when(userLoginRepository.findByUsuarioAndAtivoTrue(aluno)).thenReturn(Optional.of(userLogin));
//        when(alunoMapper.toResponse(any())).thenReturn(mock(AlunoResponse.class));
//
//        // Act
//        AlunoResponse response = alunoService.atualizarAluno(aluno.getId(), request);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals("Novo Nome", aluno.getNome());
//        assertEquals("Novo Curso", aluno.getCurso());
//
//        verify(alunoRepository).save(aluno);
//        verify(userLoginRepository, never()).save(any());
//    }
//
//    @Test
//    void deveLancarExcecao_SeNenhumaAlteracaoForFeita() {
//        // Arrange
//        Aluno aluno = criarAlunoPadrao();
//        UserLogin userLogin = criarUserLoginPadrao(aluno);
//        AlunoRequest request = criarAlunoRequestPadrao();
//
//        when(alunoRepository.findByIdAndAtivoTrue(aluno.getId())).thenReturn(Optional.of(aluno));
//        when(userLoginRepository.findByUsuarioAndAtivoTrue(aluno)).thenReturn(Optional.of(userLogin));
//        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true); // Simula senha igual
//
//        // Act & Assert
//        assertThrows(NenhumaAlteracaoRealizadaException.class,
//                () -> alunoService.atualizarAluno(aluno.getId(), request));
//
//        verify(alunoRepository, never()).save(any());
//        verify(userLoginRepository, never()).save(any());
//    }
//
//    @Test
//    void deveLancarExcecao_SeAlunoNaoForEncontrado() {
//        when(alunoRepository.findByIdAndAtivoTrue(2L)).thenReturn(Optional.empty());
//
//        assertThrows(AlunoNaoEncontradoException.class,
//                () -> alunoService.atualizarAluno(2L, criarAlunoRequestPadrao()));
//    }
//
//    @Test
//    void deveLancarExcecao_SeUserLoginNaoForEncontrado() {
//        Aluno aluno = criarAlunoPadrao();
//
//        when(alunoRepository.findByIdAndAtivoTrue(aluno.getId())).thenReturn(Optional.of(aluno));
//        when(userLoginRepository.findByUsuarioAndAtivoTrue(aluno)).thenReturn(Optional.empty());
//
//        assertThrows(RuntimeException.class,
//                () -> alunoService.atualizarAluno(aluno.getId(), criarAlunoRequestPadrao()));
//    }
//
//    @Test
//    void deveLancarExcecao_SeSenhaInformadaForIgual() {
//        Aluno aluno = criarAlunoPadrao();
//        UserLogin userLogin = criarUserLoginPadrao(aluno);
//
//        AlunoRequest request = new AlunoRequest(
//                aluno.getNome(), aluno.getEmail(), "Senha@123",
//                aluno.getCpf(), aluno.getCurso()
//        );
//
//        when(alunoRepository.findByIdAndAtivoTrue(aluno.getId())).thenReturn(Optional.of(aluno));
//        when(userLoginRepository.findByUsuarioAndAtivoTrue(aluno)).thenReturn(Optional.of(userLogin));
//        when(passwordEncoder.matches("Senha@123", aluno.getSenha())).thenReturn(true);
//
//        assertThrows(NenhumaAlteracaoRealizadaException.class,
//                () -> alunoService.atualizarAluno(aluno.getId(), request));
//
//        verify(alunoRepository, never()).save(any());
//        verify(userLoginRepository, never()).save(any());
//    }
//
//    @Test
//    void deveAtualizarSenha_SeInformadaDiferente() {
//        Aluno aluno = criarAlunoPadrao();
//        UserLogin userLogin = criarUserLoginPadrao(aluno);
//
//        AlunoRequest request = new AlunoRequest(
//                aluno.getNome(), aluno.getEmail(), "NovaSenha@123",
//                aluno.getCpf(), aluno.getCurso()
//        );
//
//        when(alunoRepository.findByIdAndAtivoTrue(aluno.getId())).thenReturn(Optional.of(aluno));
//        when(userLoginRepository.findByUsuarioAndAtivoTrue(aluno)).thenReturn(Optional.of(userLogin));
//        when(passwordEncoder.encode("NovaSenha@123")).thenReturn("NovaSenhaCriptografada");
//
//        alunoService.atualizarAluno(aluno.getId(), request);
//
//        assertEquals("NovaSenhaCriptografada", aluno.getSenha());
//        assertEquals("NovaSenhaCriptografada", userLogin.getSenha());
//
//        verify(alunoRepository).save(aluno);
//        verify(userLoginRepository).save(userLogin);
//    }
//
//    @Test
//    void deveAtualizarEmail_SeInformadoDiferente() {
//        Aluno aluno = criarAlunoPadrao();
//        UserLogin userLogin = criarUserLoginPadrao(aluno);
//
//        AlunoRequest request = new AlunoRequest(
//                aluno.getNome(), "novo@email.com", null,
//                aluno.getCpf(), aluno.getCurso()
//        );
//
//        when(alunoRepository.findByIdAndAtivoTrue(aluno.getId())).thenReturn(Optional.of(aluno));
//        when(userLoginRepository.findByUsuarioAndAtivoTrue(aluno)).thenReturn(Optional.of(userLogin));
//
//        alunoService.atualizarAluno(aluno.getId(), request);
//
//        assertEquals("novo@email.com", aluno.getEmail());
//        assertEquals("novo@email.com", userLogin.getEmail());
//
//        verify(alunoRepository).save(aluno);
//        verify(userLoginRepository).save(userLogin);
//    }
//
//    @Test
//    void deveAtualizarEmailESenha_SeAmbosInformados() {
//        Aluno aluno = criarAlunoPadrao();
//        UserLogin userLogin = criarUserLoginPadrao(aluno);
//
//        AlunoRequest request = new AlunoRequest(
//                aluno.getNome(), "novo@email.com", "NovaSenha@123",
//                aluno.getCpf(), aluno.getCurso()
//        );
//
//        when(alunoRepository.findByIdAndAtivoTrue(aluno.getId())).thenReturn(Optional.of(aluno));
//        when(userLoginRepository.findByUsuarioAndAtivoTrue(aluno)).thenReturn(Optional.of(userLogin));
//        when(passwordEncoder.encode("NovaSenha@123")).thenReturn("NovaSenhaCriptografada");
//
//        alunoService.atualizarAluno(aluno.getId(), request);
//
//        assertEquals("novo@email.com", aluno.getEmail());
//        assertEquals("novo@email.com", userLogin.getEmail());
//        assertEquals("NovaSenhaCriptografada", aluno.getSenha());
//        assertEquals("NovaSenhaCriptografada", userLogin.getSenha());
//
//        verify(alunoRepository).save(aluno);
//        verify(userLoginRepository).save(userLogin);
//    }
//}
