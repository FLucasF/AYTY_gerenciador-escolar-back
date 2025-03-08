//package br.com.ufpb.GerenciadorEscolar.service.mural;
//
//import br.com.ufpb.GerenciadorEscolar.service.PostagemNaoEncontradaException;
//import br.com.ufpb.GerenciadorEscolar.model.Mural;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class MuralServiceImplDeletarTest extends BaseMuralServiceTest {
//
//    @Test
//    void deveDesativarPostagem_QuandoExistir() {
//        // Arrange
//        Long idPostagem = 1L;
//        Mural mural = criarMuralAtivo(); // Garante que a postagem está ativa
//
//        // Mockando o repositório para retornar a postagem ativa
//        when(muralRepository.findByIdAndAtivoTrue(idPostagem)).thenReturn(Optional.of(mural));
//
//        // Act
//        muralService.deletarPostagem(idPostagem);
//
//        // Assert
//        assertFalse(mural.isAtivo(), "A postagem deve ser desativada");
//
//        // Verifica se `save()` foi chamado corretamente
//        verify(muralRepository, times(1)).save(mural);
//    }
//
//
//    @Test
//    void deveLancarPostagemNaoEncontradaException_QuandoNaoExistir() {
//        // Arrange
//        Long idPostagem = 99L;
//        when(muralRepository.findById(idPostagem)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        PostagemNaoEncontradaException exception = assertThrows(
//                PostagemNaoEncontradaException.class,
//                () -> muralService.deletarPostagem(idPostagem)
//        );
//
//        assertEquals("Postagem não encontrada", exception.getMessage());
//
//        // Garante que `save` nunca é chamado se a postagem não for encontrada
//        verify(muralRepository, never()).save(any(Mural.class));
//    }
//
//    @Test
//    void deveLancarPostagemNaoEncontradaException_QuandoPostagemJaEstaDesativada() {
//        // Arrange
//        Long idPostagem = 1L;
//
//        // Simula que a postagem já está desativada, então findByIdAndAtivoTrue retorna Optional.empty()
//        when(muralRepository.findByIdAndAtivoTrue(idPostagem)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        PostagemNaoEncontradaException exception = assertThrows(
//                PostagemNaoEncontradaException.class,
//                () -> muralService.deletarPostagem(idPostagem)
//        );
//
//        assertEquals("Postagem não encontrada", exception.getMessage());
//
//        verify(muralRepository, times(1)).findByIdAndAtivoTrue(idPostagem);
//        verify(muralRepository, never()).save(any(Mural.class));
//    }
//
//}
