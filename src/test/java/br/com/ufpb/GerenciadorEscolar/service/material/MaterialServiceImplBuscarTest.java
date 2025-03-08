package br.com.ufpb.GerenciadorEscolar.service.material;

import br.com.ufpb.GerenciadorEscolar.dto.material.MaterialResponse;
import br.com.ufpb.GerenciadorEscolar.service.MinioException;
import br.com.ufpb.GerenciadorEscolar.model.Material;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MaterialServiceImplBuscarTest extends BaseMaterialServiceTest {

    @Test
    public void testBuscarMaterialPorId_ComSucesso() {
        log.info("Iniciando teste: testBuscarMaterialPorId_ComSucesso");

        // Arrange
        Material material = criarMaterialPadrao();
        String expectedMediaUrl = "https://minio.com/file123";

        when(materialRepository.findById(eq(material.getId()))).thenReturn(Optional.of(material));

        // Mock do WebClient para GET request
        doReturn(requestHeadersUriSpec).when(webClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(any(String.class));
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        doReturn(responseSpec).when(responseSpec).onStatus(any(), any());
        doReturn(Mono.just(expectedMediaUrl)).when(responseSpec).bodyToMono(eq(String.class));

        // Act
        log.info("Chamando buscarMaterialPorId para ID: {}", material.getId());
        MaterialResponse response = materialService.buscarMaterialPorId(material.getId());

        // Correção nas asserções do teste de sucesso:
        assertNotNull(response);
        assertEquals(material.getId(), response.id());
        assertEquals(material.getArquivoId(), response.nomeArquivo()); // ✅ Correção aqui
        assertEquals(expectedMediaUrl, response.urlArquivo()); // ✅ Correção aqui


        verify(materialRepository).findById(eq(material.getId()));
        verify(webClient).get();

        log.info("Teste testBuscarMaterialPorId_ComSucesso finalizado com sucesso.");
    }

    @Test
    public void testBuscarMaterialPorId_MaterialNaoEncontrado() {
        log.info("Iniciando teste: testBuscarMaterialPorId_MaterialNaoEncontrado");

        // Arrange
        Long materialId = 99L;
        when(materialRepository.findById(eq(materialId))).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                materialService.buscarMaterialPorId(materialId));

        assertEquals("Material não encontrado", exception.getMessage());

        verify(materialRepository).findById(eq(materialId));
        verifyNoInteractions(webClient);

        log.info("Teste testBuscarMaterialPorId_MaterialNaoEncontrado finalizado com sucesso.");
    }

    @Test
    public void testBuscarMaterialPorId_Erro401MinIO() {
        log.info("Iniciando teste: testBuscarMaterialPorId_Erro401MinIO");

        // Arrange
        Material material = criarMaterialPadrao();
        when(materialRepository.findById(eq(material.getId()))).thenReturn(Optional.of(material));

        doReturn(requestHeadersUriSpec).when(webClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(any(String.class));
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        doReturn(responseSpec).when(responseSpec).onStatus(any(), any());

        // Simular erro 401
        doReturn(Mono.error(new MinioException("Erro 401 - API Key inválida ou ausente", 401)))
                .when(responseSpec).bodyToMono(eq(String.class));

        // Act & Assert
        MinioException exception = assertThrows(MinioException.class, () ->
                materialService.buscarMaterialPorId(material.getId()));

        assertEquals("Erro 401 - API Key inválida ou ausente", exception.getMessage());
        assertEquals(401, exception.getStatusCode());

        verify(materialRepository).findById(eq(material.getId()));
        verify(webClient).get();

        log.info("Teste testBuscarMaterialPorId_Erro401MinIO finalizado com sucesso.");
    }

    @Test
    public void testBuscarMaterialPorId_Erro404MinIO() {
        log.info("Iniciando teste: testBuscarMaterialPorId_Erro404MinIO");

        // Arrange
        Material material = criarMaterialPadrao();
        when(materialRepository.findById(eq(material.getId()))).thenReturn(Optional.of(material));

        doReturn(requestHeadersUriSpec).when(webClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(any(String.class));
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        doReturn(responseSpec).when(responseSpec).onStatus(any(), any());

        // Simular erro 404
        doReturn(Mono.error(new MinioException("Erro 404 - Mídia não encontrada", 404)))
                .when(responseSpec).bodyToMono(eq(String.class));

        // Act & Assert
        MinioException exception = assertThrows(MinioException.class, () ->
                materialService.buscarMaterialPorId(material.getId()));

        assertEquals("Erro 404 - Mídia não encontrada", exception.getMessage());
        assertEquals(404, exception.getStatusCode());

        verify(materialRepository).findById(eq(material.getId()));
        verify(webClient).get();

        log.info("Teste testBuscarMaterialPorId_Erro404MinIO finalizado com sucesso.");
    }

    @Test
    public void testBuscarMaterialPorId_Erro500MinIO() {
        log.info("Iniciando teste: testBuscarMaterialPorId_Erro500MinIO");

        // Arrange
        Material material = criarMaterialPadrao();
        when(materialRepository.findById(eq(material.getId()))).thenReturn(Optional.of(material));

        doReturn(requestHeadersUriSpec).when(webClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(any(String.class));
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        doReturn(responseSpec).when(responseSpec).onStatus(any(), any());

        // Simular erro 500
        doReturn(Mono.error(new MinioException("Erro 500 - Erro interno ao buscar mídia", 500)))
                .when(responseSpec).bodyToMono(eq(String.class));

        // Act & Assert
        MinioException exception = assertThrows(MinioException.class, () ->
                materialService.buscarMaterialPorId(material.getId()));

        assertEquals("Erro 500 - Erro interno ao buscar mídia", exception.getMessage());
        assertEquals(500, exception.getStatusCode());

        verify(materialRepository).findById(eq(material.getId()));
        verify(webClient).get();

        log.info("Teste testBuscarMaterialPorId_Erro500MinIO finalizado com sucesso.");
    }

    @Test
    public void testBuscarMaterialPorId_ErroInesperado() {
        log.info("Iniciando teste: testBuscarMaterialPorId_ErroInesperado");

        // Arrange
        Material material = criarMaterialPadrao();
        when(materialRepository.findById(eq(material.getId()))).thenReturn(Optional.of(material));

        doReturn(requestHeadersUriSpec).when(webClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(any(String.class));
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        doReturn(responseSpec).when(responseSpec).onStatus(any(), any());

        // Simular retorno nulo
        doReturn(Mono.empty()).when(responseSpec).bodyToMono(eq(String.class));

        // Act & Assert
        MinioException exception = assertThrows(MinioException.class, () ->
                materialService.buscarMaterialPorId(material.getId()));

        assertEquals("Erro inesperado: URL da mídia é nula", exception.getMessage());
        assertEquals(500, exception.getStatusCode());

        verify(materialRepository).findById(eq(material.getId()));
        verify(webClient).get();

        log.info("Teste testBuscarMaterialPorId_ErroInesperado finalizado com sucesso.");
    }
}
