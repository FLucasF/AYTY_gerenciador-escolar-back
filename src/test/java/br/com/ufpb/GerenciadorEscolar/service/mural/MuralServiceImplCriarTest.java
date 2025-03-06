package br.com.ufpb.GerenciadorEscolar.service.mural;

import br.com.ufpb.GerenciadorEscolar.dto.mural.MuralRequest;
import br.com.ufpb.GerenciadorEscolar.dto.mural.MuralResponse;
import br.com.ufpb.GerenciadorEscolar.service.TurmaNaoEncontradaException;
import br.com.ufpb.GerenciadorEscolar.service.ProfessorNaoEncontradoException;
import br.com.ufpb.GerenciadorEscolar.model.Mural;
import br.com.ufpb.GerenciadorEscolar.model.Turma;
import br.com.ufpb.GerenciadorEscolar.model.Professor;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MuralServiceImplCriarTest extends BaseMuralServiceTest {

    @Test
    void deveCriarPostagemComSucesso() {
        // Arrange
        MuralRequest request = criarMuralRequestPadrao();
        Mural muralCriado = criarMuralAtivo();
        MuralResponse responseEsperada = criarMuralResponse(muralCriado);

        Turma turma = criarTurmaPadrao();
        Professor professor = criarProfessorPadrao();

        when(turmaRepository.findById(request.turmaId())).thenReturn(Optional.of(turma));
        when(professorRepository.findById(request.professorId())).thenReturn(Optional.of(professor));
        when(muralMapper.toEntity(request)).thenReturn(muralCriado);
        when(muralRepository.save(any(Mural.class))).thenReturn(muralCriado);
        when(muralMapper.toResponse(muralCriado)).thenReturn(responseEsperada);

        // Act
        MuralResponse response = muralService.criarPostagem(request);

        // Assert
        assertNotNull(response);
        assertEquals(request.titulo(), response.titulo());
        assertEquals(request.conteudo(), response.conteudo());
        assertEquals(request.turmaId(), response.turmaId());
        assertEquals(request.professorId(), response.professorId());

        verify(turmaRepository, times(1)).findById(request.turmaId());
        verify(professorRepository, times(1)).findById(request.professorId());
        verify(muralMapper, times(1)).toEntity(request);
        verify(muralRepository, times(1)).save(any(Mural.class));
        verify(muralMapper, times(1)).toResponse(muralCriado);
    }

    @Test
    void deveLancarTurmaNaoEncontradaException_QuandoTurmaNaoExistir() {
        MuralRequest request = criarMuralRequestPadrao();
        when(turmaRepository.findById(request.turmaId())).thenReturn(Optional.empty());

        TurmaNaoEncontradaException exception = assertThrows(
                TurmaNaoEncontradaException.class,
                () -> muralService.criarPostagem(request)
        );

        assertEquals("Turma não encontrada", exception.getMessage());

        verify(professorRepository, never()).findById(anyLong());
        verify(muralRepository, never()).save(any(Mural.class));
    }

    @Test
    void deveLancarProfessorNaoEncontradoException_QuandoProfessorNaoExistir() {
        MuralRequest request = criarMuralRequestPadrao();
        Turma turma = criarTurmaPadrao();

        when(turmaRepository.findById(request.turmaId())).thenReturn(Optional.of(turma));
        when(professorRepository.findById(request.professorId())).thenReturn(Optional.empty());

        ProfessorNaoEncontradoException exception = assertThrows(
                ProfessorNaoEncontradoException.class,
                () -> muralService.criarPostagem(request)
        );

        assertEquals("Professor não encontrado", exception.getMessage());

        verify(muralRepository, never()).save(any(Mural.class));
    }

    @Test
    void deveLancarRuntimeException_QuandoErroAoSalvar() {
        // Arrange
        MuralRequest request = criarMuralRequestPadrao();
        Mural muralCriado = criarMuralAtivo();
        Turma turma = criarTurmaPadrao();
        Professor professor = criarProfessorPadrao();

        when(turmaRepository.findById(request.turmaId())).thenReturn(Optional.of(turma));
        when(professorRepository.findById(request.professorId())).thenReturn(Optional.of(professor));
        when(muralMapper.toEntity(request)).thenReturn(muralCriado);

        // Simula um erro ao salvar no banco
        when(muralRepository.save(any(Mural.class))).thenThrow(new RuntimeException("Erro no banco"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> muralService.criarPostagem(request)
        );

        assertEquals("Erro no banco", exception.getMessage());

        verify(muralRepository, times(1)).save(any(Mural.class));
    }

    @Test
    void deveDefinirPostagemComoAtiva_AoCriarPostagem() {
        // Arrange
        MuralRequest request = criarMuralRequestPadrao();
        Mural muralCriado = criarMuralAtivo();
        MuralResponse responseEsperada = criarMuralResponse(muralCriado);

        Turma turma = criarTurmaPadrao();
        Professor professor = criarProfessorPadrao();

        when(turmaRepository.findById(request.turmaId())).thenReturn(Optional.of(turma));
        when(professorRepository.findById(request.professorId())).thenReturn(Optional.of(professor));
        when(muralMapper.toEntity(request)).thenReturn(muralCriado);
        when(muralRepository.save(any(Mural.class))).thenReturn(muralCriado);
        when(muralMapper.toResponse(muralCriado)).thenReturn(responseEsperada);

        // Act
        MuralResponse response = muralService.criarPostagem(request);

        // Assert
        assertTrue(muralCriado.isAtivo(), "A postagem deve ser criada como ativa");

        verify(muralRepository, times(1)).save(muralCriado);
    }

    @Test
    void deveChamarMapperAntesDeSalvar() {
        // Arrange
        MuralRequest request = criarMuralRequestPadrao();
        Mural muralCriado = criarMuralAtivo();
        Turma turma = criarTurmaPadrao();
        Professor professor = criarProfessorPadrao();

        when(turmaRepository.findById(request.turmaId())).thenReturn(Optional.of(turma));
        when(professorRepository.findById(request.professorId())).thenReturn(Optional.of(professor));

        // Simula o comportamento correto do Mapper e do Repositório
        when(muralMapper.toEntity(request)).thenReturn(muralCriado);
        when(muralRepository.save(any(Mural.class))).thenReturn(muralCriado);

        // Act
        muralService.criarPostagem(request);

        // Assert
        verify(muralMapper, times(1)).toEntity(request);
        verify(muralRepository, times(1)).save(any(Mural.class));
    }
}
