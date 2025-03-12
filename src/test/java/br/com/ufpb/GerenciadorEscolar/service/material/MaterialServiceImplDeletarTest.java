//package br.com.ufpb.GerenciadorEscolar.service.material;
//
//import br.com.ufpb.GerenciadorEscolar.service.MinioException;
//import br.com.ufpb.GerenciadorEscolar.model.Material;
//import org.junit.jupiter.api.Test;
//import org.springframework.http.HttpStatus;
//import reactor.core.publisher.Mono;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//public class MaterialServiceImplDeletarTest extends BaseMaterialServiceTest {
//
//    @Test
//    public void testDeletarMaterial_ComSucesso() {
//        log.info("Iniciando teste: testDeletarMaterial_ComSucesso");
//
//        // Arrange
//        Material material = criarMaterialPadrao();
//        when(materialRepository.findById(eq(material.getId()))).thenReturn(Optional.of(material));
//
//        // Mock do WebClient para retorno de sucesso (204 No Content)
//        doReturn(requestHeadersUriSpec).when(webClient).delete();
//        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(any(String.class));
//        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
//        doReturn(Mono.empty()).when(responseSpec).toBodilessEntity();
//
//        // Act
//        log.info("Chamando deletarMaterial para ID: {}", material.getId());
//        materialService.deletarMaterial(material.getId());
//
//        // Assert
//        assertFalse(material.isAtivo(), "O material deve ser desativado após a exclusão.");
//        verify(materialRepository).findById(eq(material.getId()));
//        verify(materialRepository).save(material);
//        verify(webClient).delete();
//
//        log.info("Teste testDeletarMaterial_ComSucesso finalizado com sucesso.");
//    }
//
//    @Test
//    public void testDeletarMaterial_NaoEncontrado() {
//        log.info("Iniciando teste: testDeletarMaterial_NaoEncontrado");
//
//        // Arrange
//        Long materialId = 99L;
//        when(materialRepository.findById(eq(materialId))).thenReturn(Optional.empty());
//
//        // Act & Assert
//        RuntimeException exception = assertThrows(RuntimeException.class, () ->
//                materialService.deletarMaterial(materialId));
//
//        assertEquals("Material não encontrado", exception.getMessage());
//
//        verify(materialRepository).findById(eq(materialId));
//        verifyNoInteractions(webClient);
//
//        log.info("Teste testDeletarMaterial_NaoEncontrado finalizado com sucesso.");
//    }
//
//    @Test
//    public void testDeletarMaterial_Erro401MinIO() {
//        log.info("Iniciando teste: testDeletarMaterial_Erro401MinIO");
//
//        // Arrange
//        Material material = criarMaterialPadrao();
//        when(materialRepository.findById(eq(material.getId()))).thenReturn(Optional.of(material));
//
//        // Simular erro 401 no MinIO
//        doReturn(requestHeadersUriSpec).when(webClient).delete();
//        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(any(String.class));
//        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
//        doReturn(Mono.error(new MinioException("Erro 401 - API Key inválida ou ausente", 401))).when(responseSpec).toBodilessEntity();
//
//        // Act & Assert
//        MinioException exception = assertThrows(MinioException.class, () ->
//                materialService.deletarMaterial(material.getId()));
//
//        assertEquals("Erro 401 - API Key inválida ou ausente", exception.getMessage());
//        assertEquals(401, exception.getStatusCode());
//
//        verify(materialRepository).findById(eq(material.getId()));
//        verify(webClient).delete();
//
//        log.info("Teste testDeletarMaterial_Erro401MinIO finalizado com sucesso.");
//    }
//
//    @Test
//    public void testDeletarMaterial_Erro403MinIO() {
//        log.info("Iniciando teste: testDeletarMaterial_Erro403MinIO");
//
//        // Arrange
//        Material material = criarMaterialPadrao();
//        when(materialRepository.findById(eq(material.getId()))).thenReturn(Optional.of(material));
//
//        // Simular erro 403 no MinIO
//        doReturn(requestHeadersUriSpec).when(webClient).delete();
//        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(any(String.class));
//        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
//        doReturn(Mono.error(new MinioException("Erro 403 - Sem permissão para excluir esta mídia.", 403))).when(responseSpec).toBodilessEntity();
//
//        // Act & Assert
//        MinioException exception = assertThrows(MinioException.class, () ->
//                materialService.deletarMaterial(material.getId()));
//
//        assertEquals("Erro 403 - Sem permissão para excluir esta mídia.", exception.getMessage());
//        assertEquals(403, exception.getStatusCode());
//
//        verify(materialRepository).findById(eq(material.getId()));
//        verify(webClient).delete();
//
//        log.info("Teste testDeletarMaterial_Erro403MinIO finalizado com sucesso.");
//    }
//
//    @Test
//    public void testDeletarMaterial_Erro404MinIO() {
//        log.info("Iniciando teste: testDeletarMaterial_Erro404MinIO");
//
//        // Arrange
//        Material material = criarMaterialPadrao();
//        when(materialRepository.findById(eq(material.getId()))).thenReturn(Optional.of(material));
//
//        // Simular erro 404 no MinIO
//        doReturn(requestHeadersUriSpec).when(webClient).delete();
//        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(any(String.class));
//        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
//        doReturn(Mono.error(new MinioException("Erro 404 - Mídia não encontrada para exclusão.", 404))).when(responseSpec).toBodilessEntity();
//
//        // Act & Assert
//        MinioException exception = assertThrows(MinioException.class, () ->
//                materialService.deletarMaterial(material.getId()));
//
//        assertEquals("Erro 404 - Mídia não encontrada para exclusão.", exception.getMessage());
//        assertEquals(404, exception.getStatusCode());
//
//        verify(materialRepository).findById(eq(material.getId()));
//        verify(webClient).delete();
//
//        log.info("Teste testDeletarMaterial_Erro404MinIO finalizado com sucesso.");
//    }
//
//    @Test
//    public void testDeletarMaterial_Erro500MinIO() {
//        log.info("Iniciando teste: testDeletarMaterial_Erro500MinIO");
//
//        // Arrange
//        Material material = criarMaterialPadrao();
//        when(materialRepository.findById(eq(material.getId()))).thenReturn(Optional.of(material));
//
//        // Simular erro 500 no MinIO
//        doReturn(requestHeadersUriSpec).when(webClient).delete();
//        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(any(String.class));
//        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
//        doReturn(Mono.error(new MinioException("Erro 500 - Erro interno ao remover mídia", 500))).when(responseSpec).toBodilessEntity();
//
//        // Act & Assert
//        MinioException exception = assertThrows(MinioException.class, () ->
//                materialService.deletarMaterial(material.getId()));
//
//        assertEquals("Erro 500 - Erro interno ao remover mídia", exception.getMessage());
//        assertEquals(500, exception.getStatusCode());
//
//        verify(materialRepository).findById(eq(material.getId()));
//        verify(webClient).delete();
//
//        log.info("Teste testDeletarMaterial_Erro500MinIO finalizado com sucesso.");
//    }
//}
