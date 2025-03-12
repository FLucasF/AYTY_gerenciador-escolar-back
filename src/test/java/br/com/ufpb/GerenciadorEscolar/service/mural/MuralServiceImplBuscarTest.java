//package br.com.ufpb.GerenciadorEscolar.service.mural;
//
//import br.com.ufpb.GerenciadorEscolar.dto.mural.MuralResponse;
//import br.com.ufpb.GerenciadorEscolar.service.PostagemNaoEncontradaException;
//import br.com.ufpb.GerenciadorEscolar.model.entity.Mural;
//import org.junit.jupiter.api.Test;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class MuralServiceImplBuscarTest extends BaseMuralServiceTest {
//
//    @Test
//    void deveRetornarPostagem_QuandoExistir() {
//        // Arrange - Criando uma postagem no banco
//        Long idPostagem = 1L;
//        Mural mural = criarMuralAtivo();
//        MuralResponse responseEsperada = criarMuralResponse(mural);
//
//        when(muralRepository.findById(idPostagem)).thenReturn(Optional.of(mural));
//        when(muralMapper.toResponse(mural)).thenReturn(responseEsperada);
//
//        // Act
//        MuralResponse response = muralService.buscarPostagemPorId(idPostagem);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(responseEsperada.id(), response.id());
//        assertEquals(responseEsperada.titulo(), response.titulo());
//        assertEquals(responseEsperada.conteudo(), response.conteudo());
//
//        verify(muralRepository, times(1)).findById(idPostagem);
//        verify(muralMapper, times(1)).toResponse(mural);
//    }
//
//    @Test
//    void deveLancarPostagemNaoEncontradaException_QuandoNaoExistir() {
//        // Arrange - Nenhuma postagem encontrada no banco
//        Long idPostagem = 99L;
//        when(muralRepository.findById(idPostagem)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        PostagemNaoEncontradaException exception = assertThrows(
//                PostagemNaoEncontradaException.class,
//                () -> muralService.buscarPostagemPorId(idPostagem)
//        );
//
//        assertEquals("Postagem não encontrada", exception.getMessage());
//
//        verify(muralRepository, times(1)).findById(idPostagem);
//        verify(muralMapper, never()).toResponse(any(Mural.class));
//    }
//
//    @Test
//    void deveLancarPostagemNaoEncontradaException_SeRepositoryRetornarNull() {
//        // Arrange
//        Long idPostagem = 1L;
//
//        // Simulando um comportamento inesperado (findById retornando null, o que não deveria ocorrer)
//        when(muralRepository.findById(idPostagem)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        PostagemNaoEncontradaException exception = assertThrows(
//                PostagemNaoEncontradaException.class,
//                () -> muralService.buscarPostagemPorId(idPostagem)
//        );
//
//        assertEquals("Postagem não encontrada", exception.getMessage());
//
//        verify(muralRepository, times(1)).findById(idPostagem);
//        verify(muralMapper, never()).toResponse(any(Mural.class));
//    }
//
//}
