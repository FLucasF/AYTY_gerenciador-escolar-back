package br.com.ufpb.GerenciadorEscolar.service.aluno;

import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorRequest;
import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorResponse;
import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoRequest;
import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoResponse;
import br.com.ufpb.GerenciadorEscolar.model.Administrador;
import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class AlunoServiceImplDesativarTest extends BaseAlunoServiceTest {

    @Test
    public void testDesativarAluno_Success() {
        Long id = 1L;
        Aluno aluno = new Aluno();
        aluno.setId(id);
        aluno.setAtivo(true);  // O aluno está ativo inicialmente
        aluno.setNome("Nome do Aluno");
        aluno.setEmail("email@teste.com");
        aluno.setCpf("12345678901");
        aluno.setCurso("Curso A");

        // Simulando o comportamento do repositório para encontrar o aluno
        when(alunoRepository.findById(id)).thenReturn(Optional.of(aluno));

        // Chama o método que estamos testando
        alunoService.desativarAluno(id);

        // Verifica se o aluno foi desativado
        assertFalse(aluno.isAtivo(), "O aluno deveria estar desativado.");

        // Verifica se o repositório foi chamado corretamente para salvar a alteração
        verify(alunoRepository, times(1)).save(aluno);
    }

    @Test
    public void testDesativarAluno_AlunoNaoEncontrado() {
        Long id = 1L;

        // Simulando que o aluno não é encontrado
        when(alunoRepository.findById(id)).thenReturn(Optional.empty());

        // Chama o método e verifica se a exceção é lançada
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            alunoService.desativarAluno(id);
        });

        // Verifica a mensagem de erro da exceção
        assertEquals("Aluno não encontrado", exception.getMessage());
    }

}
