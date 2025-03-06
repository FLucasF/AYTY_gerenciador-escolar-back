package br.com.ufpb.GerenciadorEscolar.service.mural;

import br.com.ufpb.GerenciadorEscolar.dto.mural.MuralResponse;
import br.com.ufpb.GerenciadorEscolar.model.Mural;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MuralServiceImplListarTest extends BaseMuralServiceTest {

    @Test
    void deveRetornarPaginaDePostagens_QuandoExistiremPostagens() {
        // Arrange
        Long idTurma = 1L;
        Pageable pageable = PageRequest.of(0, 2, Sort.by("dataCriacao").descending());

        // Criando postagens simuladas
        Mural mural1 = criarMuralAtivo();
        Mural mural2 = criarMuralAtivo();
        List<Mural> murais = List.of(mural1, mural2);
        Page<Mural> pageDeMurais = new PageImpl<>(murais, pageable, murais.size());

        MuralResponse response1 = criarMuralResponse(mural1);
        MuralResponse response2 = criarMuralResponse(mural2);

        when(muralRepository.findByTurmaIdAndAtivoTrue(idTurma, pageable)).thenReturn(pageDeMurais);
        when(muralMapper.toResponse(mural1)).thenReturn(response1);
        when(muralMapper.toResponse(mural2)).thenReturn(response2);

        // Act
        Page<MuralResponse> responsePage = muralService.listarPostagensPorTurma(idTurma, pageable);

        // Assert
        assertNotNull(responsePage);
        assertEquals(2, responsePage.getTotalElements());
        assertEquals(response1, responsePage.getContent().get(0));
        assertEquals(response2, responsePage.getContent().get(1));

        // Verifica se o repositório foi chamado corretamente
        verify(muralRepository, times(1)).findByTurmaIdAndAtivoTrue(idTurma, pageable);
        verify(muralMapper, times(2)).toResponse(any(Mural.class));
    }

    @Test
    void deveRetornarPaginaVazia_QuandoNaoExistiremPostagens() {
        // Arrange
        Long idTurma = 1L;
        Pageable pageable = PageRequest.of(0, 2);
        Page<Mural> pageVazia = Page.empty();

        when(muralRepository.findByTurmaIdAndAtivoTrue(idTurma, pageable)).thenReturn(pageVazia);

        // Act
        Page<MuralResponse> responsePage = muralService.listarPostagensPorTurma(idTurma, pageable);

        // Assert
        assertNotNull(responsePage);
        assertTrue(responsePage.isEmpty());

        // Verifica se o repositório foi chamado corretamente
        verify(muralRepository, times(1)).findByTurmaIdAndAtivoTrue(idTurma, pageable);
        verify(muralMapper, never()).toResponse(any(Mural.class));
    }

    @Test
    void deveChamarFindByTurmaIdAndAtivoTrue_ComParametrosCorretos() {
        // Arrange
        Long idTurma = 1L;
        Pageable pageable = PageRequest.of(0, 5, Sort.by("dataCriacao").descending());

        // Simula uma página vazia para capturar os argumentos
        when(muralRepository.findByTurmaIdAndAtivoTrue(anyLong(), any(Pageable.class)))
                .thenReturn(Page.empty());

        // Act
        muralService.listarPostagensPorTurma(idTurma, pageable);

        // Captura os argumentos passados ao repositório
        ArgumentCaptor<Long> idTurmaCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(muralRepository).findByTurmaIdAndAtivoTrue(idTurmaCaptor.capture(), pageableCaptor.capture());

        // Assert
        assertEquals(idTurma, idTurmaCaptor.getValue());
        assertEquals(pageable, pageableCaptor.getValue());
    }
}
