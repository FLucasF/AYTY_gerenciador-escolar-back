package br.com.ufpb.GerenciadorEscolar.service.material;

import br.com.ufpb.GerenciadorEscolar.dto.material.MaterialRequest;
import br.com.ufpb.GerenciadorEscolar.dto.material.MaterialResponse;
import br.com.ufpb.GerenciadorEscolar.mapper.MaterialMapper;
import br.com.ufpb.GerenciadorEscolar.model.Material;
import br.com.ufpb.GerenciadorEscolar.model.Professor;
import br.com.ufpb.GerenciadorEscolar.model.Turma;
import br.com.ufpb.GerenciadorEscolar.model.TipoArquivo;
import br.com.ufpb.GerenciadorEscolar.repository.MaterialRepository;
import br.com.ufpb.GerenciadorEscolar.repository.ProfessorRepository;
import br.com.ufpb.GerenciadorEscolar.repository.TurmaRepository;
import br.com.ufpb.GerenciadorEscolar.service.MaterialServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public abstract class BaseMaterialServiceTest {

    @Mock
    protected MaterialRepository materialRepository;

    @Mock
    protected TurmaRepository turmaRepository;

    @Mock
    protected ProfessorRepository professorRepository;

    @Mock
    protected MaterialMapper materialMapper;

    @Mock
    protected WebClient webClient;

    @InjectMocks
    protected MaterialServiceImpl materialService;

    // Mocks para a cadeia do WebClient
    protected WebClient.RequestBodyUriSpec requestBodyUriSpec;
    protected WebClient.RequestBodySpec requestBodySpec;
    protected WebClient.ResponseSpec responseSpec;

    // Adicione esses mocks no BaseMaterialServiceTest para GET
    protected WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;
    protected WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Configurar a cadeia de mocks do WebClient para simular a chamada POST
        requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        requestBodySpec = mock(WebClient.RequestBodySpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(String.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any(MediaType.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any(byte[].class)))
                .thenAnswer(invocation -> requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(eq(String.class))).thenReturn(Mono.just("arquivo123"));

        // Mocks para GET (buscar URL da mídia no MinIO)
        requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);

        doReturn(requestHeadersUriSpec).when(webClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(any(String.class));
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        doReturn(responseSpec).when(responseSpec).onStatus(any(), any());
        doReturn(Mono.just("https://minio.com/media123")).when(responseSpec).bodyToMono(eq(String.class));




    }

    protected MaterialRequest criarMaterialRequestPadrao() {
        return new MaterialRequest("arquivo123", "imagem.png", TipoArquivo.IMAGEM, 1L, 2L);
    }

    protected Material criarMaterialPadrao() {
        Material material = new Material();
        material.setId(1L);
        material.setArquivoId("arquivo123");
        material.setTipoArquivo(TipoArquivo.IMAGEM);
        material.setTurma(criarTurmaPadrao());
        material.setProfessor(criarProfessorPadrao());
        material.setAtivo(true);
        return material;
    }

    protected MaterialResponse criarMaterialResponse(Material material) {
        return new MaterialResponse(
                material.getId(),
                material.getArquivoId(),
                "/api/media/get/educAPI/" + material.getArquivoId(),
                material.getTurma().getId(),
                material.getProfessor().getId()
        );
    }

    protected Turma criarTurmaPadrao() {
        Turma turma = new Turma();
        turma.setId(1L);
        turma.setNome("Turma A");
        return turma;
    }

    protected Professor criarProfessorPadrao() {
        Professor professor = new Professor();
        professor.setId(2L);
        professor.setNome("Prof. João");
        return professor;
    }
}
