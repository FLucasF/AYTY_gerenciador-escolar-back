package br.com.ufpb.GerenciadorEscolar.service.aluno;

import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoResponse;
import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.repository.AlunoRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
public class AlunoServiceImplBuscarTest extends BaseAlunoServiceTest {

    @Test
    public void testBuscarAlunoPorId_Success() {
        Long id = 1L;
        Aluno aluno = new Aluno();
        aluno.setId(id);
        aluno.setAtivo(true);  // Garantindo que o aluno está ativo
        aluno.setNome("Nome do Aluno");
        aluno.setEmail("email@teste.com");
        aluno.setCpf("12345678901");
        aluno.setCurso("Curso A");

        AlunoResponse response = new AlunoResponse(
                aluno.getId(), aluno.getNome(), aluno.getEmail(), aluno.getCpf(), aluno.getCurso()
        );

        // Mock do repositório para retornar o aluno correto
        when(alunoRepository.findByIdAndAtivoTrue(id)).thenReturn(Optional.of(aluno));

        // Mock do mapper para mapear a entidade para resposta
        when(alunoMapper.toResponse(aluno)).thenReturn(response);

        // Chama o método que estamos testando
        Optional<AlunoResponse> result = alunoService.buscarAlunoPorId(id);

        // Verifica que o aluno foi encontrado
        assertTrue(result.isPresent());

        // Verifica se a resposta é igual à esperada
        assertEquals(response, result.get());

        // Verifica se o repositório foi chamado corretamente
        verify(alunoRepository, times(1)).findByIdAndAtivoTrue(id);
    }









    // Teste: Aluno não encontrado
    @Test
    public void testBuscarAlunoPorId_NotFound() {
        Long id = 1L;

        when(alunoRepository.findByIdAndAtivoTrue(id)).thenReturn(Optional.empty()); // Simulando o repositório não encontrar o aluno

        Optional<AlunoResponse> result = alunoService.buscarAlunoPorId(id);

        // Verifica que nenhum aluno foi encontrado
        assertFalse(result.isPresent(), "Aluno encontrado quando não deveria.");
        verify(alunoRepository, times(1)).findByIdAndAtivoTrue(id); // Verifica se o repositório foi chamado uma vez
    }

    // Teste: ID inválido (por exemplo, null ou menor que 0)
    @Test
    public void testBuscarAlunoPorId_InvalidId() {
        Long invalidId = -1L;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            alunoService.buscarAlunoPorId(invalidId);
        });

        assertEquals("ID não pode ser nulo ou inválido", exception.getMessage());
    }
}
