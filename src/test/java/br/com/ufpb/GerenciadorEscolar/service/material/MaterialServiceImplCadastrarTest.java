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
//public class MaterialServiceImplCadastrarTest extends BaseMaterialServiceTest {
//
//    @Test
//    public void testSalvarMaterialComSucesso() {
//        log.info("Iniciando teste: testSalvarMaterialComSucesso");
//
//        // Arrange
//        MaterialRequest request = new MaterialRequest("arquivo123", "imagem.png", TipoArquivo.IMAGEM, 1L, 2L);
//        Turma turma = criarTurmaPadrao();
//        Professor professor = criarProfessorPadrao();
//        Material material = criarMaterialPadrao();
//
//        MaterialResponse expectedResponse = new MaterialResponse(
//                material.getId(),
//                material.getArquivoId(),
//                "/api/media/get/educAPI/" + material.getArquivoId(),
//                turma.getId(),
//                professor.getId()
//        );
//
//        // Configurar os mocks
//        when(turmaRepository.findByIdAndAtivoTrue(eq(request.turmaId()))).thenReturn(Optional.of(turma));
//        when(professorRepository.findByIdAndAtivoTrue(eq(request.professorId()))).thenReturn(Optional.of(professor));
//        when(materialMapper.toEntity(eq(request))).thenReturn(material);
//        when(materialRepository.save(any(Material.class))).thenReturn(material);
//        when(materialMapper.toResponse(eq(material))).thenReturn(expectedResponse);
//
//        // Configuração correta do WebClient no mock
//        when(webClient.post()).thenReturn(requestBodyUriSpec);
//        when(requestBodyUriSpec.uri(any(String.class))).thenReturn(requestBodySpec);
//        when(requestBodySpec.contentType(any(MediaType.class))).thenReturn(requestBodySpec);
//        when(requestBodySpec.bodyValue(any(byte[].class))).thenAnswer(invocation -> requestBodySpec);
//        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
//
//// Correção aqui: garantir que onStatus retorne o próprio responseSpec
//        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
//
//        when(responseSpec.bodyToMono(eq(String.class))).thenReturn(Mono.just("arquivo123"));
//
//
//        // Act
//        log.info("Chamando salvarMaterial com request: {}", request);
//        MaterialResponse result = materialService.salvarMaterial(request, new byte[0]);
//
//        // Assert
//        assertNotNull(result, "O resultado não deve ser nulo");
//        assertEquals(expectedResponse, result, "O resultado deve ser igual ao esperado");
//
//        verify(materialRepository).save(any(Material.class));
//        verify(materialMapper).toEntity(eq(request));
//        verify(materialMapper).toResponse(eq(material));
//
//        log.info("Teste testSalvarMaterialComSucesso finalizado com sucesso.");
//    }
//
//    @Test
//    public void testSalvarMaterialComErroNoMinIO_BadRequest() {
//        log.info("Iniciando teste: testSalvarMaterialComErroNoMinIO_BadRequest");
//
//        // Arrange
//        MaterialRequest request = criarMaterialRequestPadrao();
//        Turma turma = criarTurmaPadrao();
//        Professor professor = criarProfessorPadrao();
//        Material material = criarMaterialPadrao();
//
//        when(turmaRepository.findByIdAndAtivoTrue(eq(request.turmaId()))).thenReturn(Optional.of(turma));
//        when(professorRepository.findByIdAndAtivoTrue(eq(request.professorId()))).thenReturn(Optional.of(professor));
//        when(materialMapper.toEntity(eq(request))).thenReturn(material);
//
//        // Configuração do WebClient para simular erro 400 (Parâmetros inválidos)
//        when(webClient.post()).thenReturn(requestBodyUriSpec);
//        when(requestBodyUriSpec.uri(any(String.class))).thenReturn(requestBodySpec);
//        when(requestBodySpec.contentType(any(MediaType.class))).thenReturn(requestBodySpec);
//        when(requestBodySpec.bodyValue(any(byte[].class))).thenAnswer(invocation -> requestBodySpec);
//        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
//
//        // Evita NullPointerException retornando o próprio objeto mockado em onStatus()
//        when(responseSpec.onStatus(any(), any())).thenAnswer(invocation -> responseSpec);
//
//        when(responseSpec.bodyToMono(eq(String.class)))
//                .thenReturn(Mono.error(new MinioException("Erro 400 - Parâmetros inválidos", HttpStatus.BAD_REQUEST.value())));
//
//        // Act & Assert
//        MinioException exception = assertThrows(MinioException.class, () ->
//                materialService.salvarMaterial(request, new byte[0]));
//
//        assertEquals("Erro 400 - Parâmetros inválidos", exception.getMessage());
//        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getStatusCode());
//
//        verify(materialRepository, never()).save(any(Material.class));
//
//        log.info("Teste testSalvarMaterialComErroNoMinIO_BadRequest finalizado com sucesso.");
//    }
//
//    @Test
//    public void testSalvarMaterialComErroNoMinIO_InternalServerError() {
//        log.info("Iniciando teste: testSalvarMaterialComErroNoMinIO_InternalServerError");
//
//        // Arrange
//        MaterialRequest request = criarMaterialRequestPadrao();
//        Turma turma = criarTurmaPadrao();
//        Professor professor = criarProfessorPadrao();
//        Material material = criarMaterialPadrao();
//
//        when(turmaRepository.findByIdAndAtivoTrue(eq(request.turmaId()))).thenReturn(Optional.of(turma));
//        when(professorRepository.findByIdAndAtivoTrue(eq(request.professorId()))).thenReturn(Optional.of(professor));
//        when(materialMapper.toEntity(eq(request))).thenReturn(material);
//
//        // Configuração do WebClient para simular erro 500 (Erro interno no MinIO)
//        when(webClient.post()).thenReturn(requestBodyUriSpec);
//        when(requestBodyUriSpec.uri(any(String.class))).thenReturn(requestBodySpec);
//        when(requestBodySpec.contentType(any(MediaType.class))).thenReturn(requestBodySpec);
//        when(requestBodySpec.bodyValue(any(byte[].class))).thenAnswer(invocation -> requestBodySpec);
//        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
//
//        // Evita NullPointerException retornando o próprio objeto mockado em onStatus()
//        when(responseSpec.onStatus(any(), any())).thenAnswer(invocation -> responseSpec);
//
//        when(responseSpec.bodyToMono(eq(String.class)))
//                .thenReturn(Mono.error(new MinioException("Erro 500 - Erro interno no MinIO", HttpStatus.INTERNAL_SERVER_ERROR.value())));
//
//        // Act & Assert
//        MinioException exception = assertThrows(MinioException.class, () ->
//                materialService.salvarMaterial(request, new byte[0]));
//
//        assertEquals("Erro 500 - Erro interno no MinIO", exception.getMessage());
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getStatusCode());
//
//        verify(materialRepository, never()).save(any(Material.class));
//
//        log.info("Teste testSalvarMaterialComErroNoMinIO_InternalServerError finalizado com sucesso.");
//    }
//
//}
