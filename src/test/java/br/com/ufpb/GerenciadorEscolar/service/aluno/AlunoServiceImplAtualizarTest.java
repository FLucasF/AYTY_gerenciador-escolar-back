package br.com.ufpb.GerenciadorEscolar.service.aluno;

import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoRequest;
import br.com.ufpb.GerenciadorEscolar.model.Administrador;
import br.com.ufpb.GerenciadorEscolar.service.NenhumaAlteracaoRealizadaException;
import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.model.UserLogin;
import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoResponse;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlunoServiceImplAtualizarTest extends BaseAlunoServiceTest {

    @Test
    void deveAtualizarAlunoComSucesso() {
        Aluno aluno = criarAlunoPadrao();
        UserLogin userLogin = criarUserLoginPadrao(aluno);

        AlunoRequest request = new AlunoRequest(
                "Ana Clara", "ana@email.com", null,
                "12345678901", "Ciência da Computação"
        );

        when(alunoRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(aluno));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(aluno)).thenReturn(Optional.of(userLogin));
        when(alunoMapper.toResponse(any())).thenReturn(mock(AlunoResponse.class));

        AlunoResponse response = alunoService.atualizarAluno(1L, request);

        assertNotNull(response);
        assertEquals("Ana Clara", aluno.getNome());
        assertEquals("Ciência da Computação", aluno.getCurso());

        verify(alunoRepository).save(aluno);
        verify(userLoginRepository).save(userLogin);
    }

    @Test
    void deveLancarExcecaoQuandoNenhumaAlteracaoForFeita() {
        Aluno aluno = criarAlunoPadrao();
        UserLogin userLogin = criarUserLoginPadrao(aluno);

        AlunoRequest request = new AlunoRequest(
                aluno.getNome(), aluno.getEmail(), null,
                aluno.getCpf(), aluno.getCurso()
        );

        when(alunoRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(aluno));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(aluno)).thenReturn(Optional.of(userLogin));

        assertThrows(NenhumaAlteracaoRealizadaException.class,
                () -> alunoService.atualizarAluno(1L, request));
    }

    @Test
    void deveLancarExcecaoQuandoAlunoNaoEncontrado() {
        when(alunoRepository.findByIdAndAtivoTrue(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> alunoService.atualizarAluno(2L, new AlunoRequest(
                        "Novo Nome", "novo@email.com", null, "12345678901", "Matemática"
                )));
    }

    @Test
    void deveLancarExcecaoQuandoUserLoginNaoEncontrado() {
        Aluno aluno = criarAlunoPadrao();

        when(alunoRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(aluno));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(aluno)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> alunoService.atualizarAluno(1L, new AlunoRequest(
                        "Novo Nome", "novo@email.com", null, "12345678901", "Física"
                )));
    }

    @Test
    void deveManterSenhaQuandoInformadaIgual() {
        Aluno aluno = criarAlunoPadrao();
        UserLogin userLogin = criarUserLoginPadrao(aluno);

        AlunoRequest request = new AlunoRequest(
                aluno.getNome(), aluno.getEmail(), "senhaAntiga",
                aluno.getCpf(), aluno.getCurso()
        );

        when(alunoRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(aluno));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(aluno)).thenReturn(Optional.of(userLogin));
        when(passwordEncoder.encode("senhaAntiga")).thenReturn("senhaAntiga");

        assertThrows(NenhumaAlteracaoRealizadaException.class,
                () -> alunoService.atualizarAluno(1L, request));

        verify(passwordEncoder, times(1)).encode("senhaAntiga");
        verify(alunoRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaExistente() {
        Aluno aluno = criarAlunoPadrao();

        // Criando um segundo aluno com o mesmo e-mail
        Aluno outroAluno = new Aluno();
        outroAluno.setId(2L);
        outroAluno.setNome("Pedro Silva");
        outroAluno.setEmail(aluno.getEmail()); // Mesmo e-mail

        AlunoRequest request = new AlunoRequest(
                aluno.getNome(), aluno.getEmail(), null,
                aluno.getCpf(), aluno.getCurso()
        );

        when(alunoRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(aluno));
        when(alunoRepository.findByEmailAndAtivoTrue(aluno.getEmail())).thenReturn(Optional.of(outroAluno));

        assertThrows(RuntimeException.class,
                () -> alunoService.atualizarAluno(1L, request));
    }

    @Test
    void deveAtualizarSenhaQuandoInformadaNova() {
        Aluno aluno = criarAlunoPadrao();
        UserLogin userLogin = criarUserLoginPadrao(aluno);

        AlunoRequest request = new AlunoRequest(
                aluno.getNome(), aluno.getEmail(), "novaSenha",
                aluno.getCpf(), aluno.getCurso()
        );

        when(alunoRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(aluno));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(aluno)).thenReturn(Optional.of(userLogin));
        when(passwordEncoder.encode("novaSenha")).thenReturn("senhaCriptografada");

        alunoService.atualizarAluno(1L, request);

        assertEquals("senhaCriptografada", aluno.getSenha());
        assertEquals("senhaCriptografada", userLogin.getSenha());

        verify(alunoRepository).save(aluno);
        verify(userLoginRepository).save(userLogin);
    }



    @Test
    void deveAtualizarNomeDoAluno() {
        Aluno aluno = criarAlunoPadrao();
        UserLogin userLogin = criarUserLoginPadrao(aluno);

        AlunoRequest request = new AlunoRequest(
                "Novo Nome", aluno.getEmail(), null,
                aluno.getCpf(), aluno.getCurso()
        );

        when(alunoRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(aluno));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(aluno)).thenReturn(Optional.of(userLogin));
        when(alunoMapper.toResponse(any())).thenReturn(mock(AlunoResponse.class));

        alunoService.atualizarAluno(1L, request);

        assertEquals("Novo Nome", aluno.getNome());
        verify(alunoRepository).save(aluno);
    }

    @Test
    void deveAtualizarEmailDoAluno() {
        Aluno aluno = criarAlunoPadrao();
        UserLogin userLogin = criarUserLoginPadrao(aluno);

        AlunoRequest request = new AlunoRequest(
                aluno.getNome(), "novoemail@email.com", null,
                aluno.getCpf(), aluno.getCurso()
        );

        when(alunoRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(aluno));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(aluno)).thenReturn(Optional.of(userLogin));
        when(alunoRepository.findByEmailAndAtivoTrue("novoemail@email.com")).thenReturn(Optional.empty());

        alunoService.atualizarAluno(1L, request);

        assertEquals("novoemail@email.com", aluno.getEmail());
        assertEquals("novoemail@email.com", userLogin.getEmail());

        verify(alunoRepository).save(aluno);
        verify(userLoginRepository).save(userLogin);
    }

    @Test
    void deveAtualizarCpfDoAluno() {
        Aluno aluno = criarAlunoPadrao();
        UserLogin userLogin = criarUserLoginPadrao(aluno); // Criando UserLogin

        AlunoRequest request = new AlunoRequest(
                aluno.getNome(), aluno.getEmail(), null,
                "98765432100", aluno.getCurso()
        );

        when(alunoRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(aluno));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(aluno)).thenReturn(Optional.of(userLogin)); // ✅ Simulando UserLogin

        alunoService.atualizarAluno(1L, request);

        assertEquals("98765432100", aluno.getCpf());
        verify(alunoRepository).save(aluno);
    }


    @Test
    void deveAtualizarCursoDoAluno() {
        Aluno aluno = criarAlunoPadrao();
        UserLogin userLogin = criarUserLoginPadrao(aluno); // Criando UserLogin

        AlunoRequest request = new AlunoRequest(
                aluno.getNome(), aluno.getEmail(), null,
                aluno.getCpf(), "Arquitetura"
        );

        when(alunoRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(aluno));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(aluno)).thenReturn(Optional.of(userLogin)); // ✅ Simulando UserLogin

        alunoService.atualizarAluno(1L, request);

        assertEquals("Arquitetura", aluno.getCurso());
        verify(alunoRepository).save(aluno);
    }


    @Test
    void deveAtualizarSenhaDoAluno() {
        Aluno aluno = criarAlunoPadrao();
        UserLogin userLogin = criarUserLoginPadrao(aluno);

        AlunoRequest request = new AlunoRequest(
                aluno.getNome(), aluno.getEmail(), "novaSenha",
                aluno.getCpf(), aluno.getCurso()
        );

        when(alunoRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(aluno));
        when(userLoginRepository.findByUsuarioAndAtivoTrue(aluno)).thenReturn(Optional.of(userLogin));
        when(passwordEncoder.encode("novaSenha")).thenReturn("senhaCriptografada");

        alunoService.atualizarAluno(1L, request);

        assertEquals("senhaCriptografada", aluno.getSenha());
        assertEquals("senhaCriptografada", userLogin.getSenha());

        verify(alunoRepository).save(aluno);
        verify(userLoginRepository).save(userLogin);
    }


}
