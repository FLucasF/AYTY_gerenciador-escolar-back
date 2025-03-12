//package br.com.ufpb.GerenciadorEscolar.service.material;
//
//import br.com.ufpb.GerenciadorEscolar.dto.material.MaterialRequest;
//import br.com.ufpb.GerenciadorEscolar.dto.material.MaterialResponse;
//import br.com.ufpb.GerenciadorEscolar.service.MinioException;
//import br.com.ufpb.GerenciadorEscolar.model.entity.Material;
//import br.com.ufpb.GerenciadorEscolar.model.entity.Professor;
//import br.com.ufpb.GerenciadorEscolar.model.entity.Turma;
//import br.com.ufpb.GerenciadorEscolar.model.entity.TipoArquivo;
//import org.junit.jupiter.api.Test;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import reactor.core.publisher.Mono;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//public class MaterialServiceImplAtualizarTest extends BaseMaterialServiceTest {
//
//    @Test
//    public void testAtualizarMaterialComSucesso() {
//        log.info("Iniciando teste: testAtualizarMaterialComSucesso");
//
//        // Arrange
//        Long materialId = 1L;
//        MaterialRequest request = new MaterialRequest("arquivo123", "imagem_atualizada.png", TipoArquivo.IMAGEM, 1L, 2L);
//        Turma turma = criarTurmaPadrao();
//        Professor professor = criarProfessorPadrao();
//        Material material = criarMaterialPadrao();
//        material.setId(materialId);
//
//        MaterialResponse expectedResponse = new MaterialResponse(
//                material.getId(),
//                "arquivoAtualizado123",
//                "/api/media/get/educAPI/arquivoAtualizado123",
//                turma.getId(),
//                professor.getId()
//        );
//
//        // Configuração dos mocks
//        when(materialRepository.findById(eq(materialId))).thenReturn(Optional.of(material));
//        when(turmaRepository.findById(eq(request.turmaId()))).thenReturn(Optional.of(turma));
//        when(professorRepository.findById(eq(request.professorId()))).thenReturn(Optional.of(professor));
//        when(materialRepository.save(any(Material.class))).thenReturn(material);
//        when(materialMapper.toResponse(eq(material))).thenReturn(expectedResponse);
//
//        // Mock do WebClient
//        when(webClient.put()).thenReturn(requestBodyUriSpec);
//        when(requestBodyUriSpec.uri(any(String.class))).thenReturn(requestBodySpec);
//        when(requestBodySpec.contentType(any(MediaType.class))).thenReturn(requestBodySpec);
//        when(requestBodySpec.bodyValue(any(byte[].class))).thenAnswer(invocation -> requestBodySpec);
//        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
//        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
//        when(responseSpec.bodyToMono(eq(String.class))).thenReturn(Mono.just("arquivoAtualizado123"));
//
//        // Act
//        log.info("Chamando atualizarMaterial com request: {}", request);
//        MaterialResponse result = materialService.atualizarMaterial(materialId, request, new byte[0]);
//
//        // Assert
//        assertNotNull(result, "O resultado não deve ser nulo");
//        assertEquals(expectedResponse, result, "O resultado deve ser igual ao esperado");
//
//        verify(materialRepository).save(any(Material.class));
//        verify(materialMapper).toResponse(eq(material));
//
//        log.info("Teste testAtualizarMaterialComSucesso finalizado com sucesso.");
//    }
//
//    @Test
//    public void testAtualizarMaterialNaoEncontrado() {
//        log.info("Iniciando teste: testAtualizarMaterialNaoEncontrado");
//
//        // Arrange
//        Long materialId = 1L;
//        MaterialRequest request = criarMaterialRequestPadrao();
//        when(materialRepository.findById(eq(materialId))).thenReturn(Optional.empty());
//
//        // Act & Assert
//        RuntimeException exception = assertThrows(RuntimeException.class, () ->
//                materialService.atualizarMaterial(materialId, request, new byte[0]));
//
//        assertEquals("Material não encontrado", exception.getMessage());
//
//        verify(materialRepository).findById(eq(materialId));
//        verifyNoInteractions(turmaRepository, professorRepository, materialMapper);
//
//        log.info("Teste testAtualizarMaterialNaoEncontrado finalizado com sucesso.");
//    }
//
//    @Test
//    public void testAtualizarMaterialComErroNoMinIO_BadRequest() {
//        log.info("Iniciando teste: testAtualizarMaterialComErroNoMinIO_BadRequest");
//
//        // Arrange
//        Long materialId = 1L;
//        MaterialRequest request = criarMaterialRequestPadrao();
//        Material material = criarMaterialPadrao();
//
//        when(materialRepository.findById(eq(materialId))).thenReturn(Optional.of(material));
//
//        // Simular erro 400 (Parâmetros inválidos)
//        when(webClient.put()).thenReturn(requestBodyUriSpec);
//        when(requestBodyUriSpec.uri(any(String.class))).thenReturn(requestBodySpec);
//        when(requestBodySpec.contentType(any(MediaType.class))).thenReturn(requestBodySpec);
//        when(requestBodySpec.bodyValue(any(byte[].class))).thenAnswer(invocation -> requestBodySpec);
//        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
//        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
//
//        when(responseSpec.bodyToMono(eq(String.class)))
//                .thenReturn(Mono.error(new MinioException("Erro 400 - Parâmetros inválidos", HttpStatus.BAD_REQUEST.value())));
//
//        // Act & Assert
//        MinioException exception = assertThrows(MinioException.class, () ->
//                materialService.atualizarMaterial(materialId, request, new byte[0]));
//
//        assertEquals("Erro 400 - Parâmetros inválidos", exception.getMessage());
//        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getStatusCode());
//
//        verify(materialRepository, never()).save(any(Material.class));
//
//        log.info("Teste testAtualizarMaterialComErroNoMinIO_BadRequest finalizado com sucesso.");
//    }
//
//    @Test
//    public void testAtualizarMaterialComErroNoMinIO_InternalServerError() {
//        log.info("Iniciando teste: testAtualizarMaterialComErroNoMinIO_InternalServerError");
//
//        // Arrange
//        Long materialId = 1L;
//        MaterialRequest request = criarMaterialRequestPadrao();
//        Material material = criarMaterialPadrao();
//
//        when(materialRepository.findById(eq(materialId))).thenReturn(Optional.of(material));
//
//        // Simular erro 500 (Erro interno no MinIO)
//        when(webClient.put()).thenReturn(requestBodyUriSpec);
//        when(requestBodyUriSpec.uri(any(String.class))).thenReturn(requestBodySpec);
//        when(requestBodySpec.contentType(any(MediaType.class))).thenReturn(requestBodySpec);
//        when(requestBodySpec.bodyValue(any(byte[].class))).thenAnswer(invocation -> requestBodySpec);
//        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
//        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
//
//        when(responseSpec.bodyToMono(eq(String.class)))
//                .thenReturn(Mono.error(new MinioException("Erro 500 - Erro interno no MinIO", HttpStatus.INTERNAL_SERVER_ERROR.value())));
//
//        // Act & Assert
//        MinioException exception = assertThrows(MinioException.class, () ->
//                materialService.atualizarMaterial(materialId, request, new byte[0]));
//
//        assertEquals("Erro 500 - Erro interno no MinIO", exception.getMessage());
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getStatusCode());
//
//        verify(materialRepository, never()).save(any(Material.class));
//
//        log.info("Teste testAtualizarMaterialComErroNoMinIO_InternalServerError finalizado com sucesso.");
//    }
//}
