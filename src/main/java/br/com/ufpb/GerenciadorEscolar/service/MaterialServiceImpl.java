package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.dto.material.MaterialRequest;
import br.com.ufpb.GerenciadorEscolar.dto.material.MaterialResponse;
import br.com.ufpb.GerenciadorEscolar.dto.minio.MinioResponse;
import br.com.ufpb.GerenciadorEscolar.mapper.MaterialMapper;
import br.com.ufpb.GerenciadorEscolar.model.Material;
import br.com.ufpb.GerenciadorEscolar.model.Professor;
import br.com.ufpb.GerenciadorEscolar.model.Turma;
import br.com.ufpb.GerenciadorEscolar.repository.MaterialRepository;
import br.com.ufpb.GerenciadorEscolar.repository.ProfessorRepository;
import br.com.ufpb.GerenciadorEscolar.repository.TurmaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MaterialServiceImpl implements MaterialServiceInterface {

    private final MaterialRepository materialRepository;
    private final TurmaRepository turmaRepository;
    private final ProfessorRepository professorRepository;
    private final MaterialMapper materialMapper;
    private final WebClient webClient;

    private static final String MINIO_BASE_URL = "http://localhost:8080/api/media";

    @Autowired
    public MaterialServiceImpl(MaterialRepository materialRepository,
                               TurmaRepository turmaRepository,
                               ProfessorRepository professorRepository,
                               MaterialMapper materialMapper,
                               WebClient webClient) {
        this.materialRepository = materialRepository;
        this.turmaRepository = turmaRepository;
        this.professorRepository = professorRepository;
        this.materialMapper = materialMapper;
        this.webClient = webClient;
    }

    @Override
    public MaterialResponse salvarMaterial(MaterialRequest materialRequest, byte[] file) {
        log.info("Iniciando upload do material para MinIO e salvamento no banco.");

        // Validação das entidades
        Turma turma = turmaRepository.findByIdAndAtivoTrue(materialRequest.turmaId())
                .orElseThrow(() -> new RuntimeException("Turma não encontrada"));
        log.debug("Turma encontrada: ID {}", turma.getId());

        Professor professor = professorRepository.findByIdAndAtivoTrue(materialRequest.professorId())
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));
        log.debug("Professor encontrado: ID {}", professor.getId());

        // Monta o corpo da requisição multipart
        MultiValueMap<String, Object> multipartBody = new LinkedMultiValueMap<>();
        ByteArrayResource fileResource = new ByteArrayResource(file) {
            @Override
            public String getFilename() {
                return materialRequest.nomeArquivo();
            }
        };
        multipartBody.add("file", fileResource);
        multipartBody.add("uploadedBy", professor.getId().toString());
        multipartBody.add("serviceName", Material.SERVICE_NAME); // "gerenciadorEscolar"
        multipartBody.add("entityId", turma.getId().toString());
        log.info("Multipart montado com file: {} e entityId: {}", materialRequest.nomeArquivo(), turma.getId());

        // Realiza o upload para o MinIO, aceitando qualquer Content-Type na resposta
        log.info("Enviando requisição POST para MinIO em {}", MINIO_BASE_URL);
        MinioResponse minioResponse = webClient.post()
                .uri(MINIO_BASE_URL)
                .header("api-key", "123")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.ALL)
                .body(BodyInserters.fromMultipartData(multipartBody))
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    log.error("Erro ao enviar arquivo para MinIO. Código: {}. Resposta: {}",
                                            response.statusCode().value(), body);
                                    return Mono.error(new RuntimeException("Erro ao enviar arquivo para MinIO."));
                                }))
                .bodyToMono(MinioResponse.class)
                .block();
        log.info("Resposta do MinIO recebida: {}", minioResponse);

        if (minioResponse == null || minioResponse.id() == null) {
            log.error("MinioResponse ou ID nulo. minioResponse: {}", minioResponse);
            throw new RuntimeException("Erro ao processar o upload do arquivo no MinIO.");
        }

        // Extrai somente o ID do arquivo retornado
        String arquivoId = minioResponse.id().toString();
        log.info("ID do arquivo extraído: {}", arquivoId);

        // Converte o MaterialRequest para Material e associa as entidades
        Material material = materialMapper.toEntity(materialRequest);
        material.setArquivoId(arquivoId);
        material.setTurma(turma);
        material.setProfessor(professor);
        materialRepository.save(material);
        log.info("Material salvo no banco com ID: {}", material.getId());

        MaterialResponse response = materialMapper.toResponse(material);
        log.info("MaterialResponse gerado: {}", response);
        return response;
    }

    @Override
    public Page<MaterialResponse> listarMateriaisPorEntidade(String serviceName, Long turmaId, Pageable pageable) {
        log.info("Listando materiais para Turma ID '{}' e serviço '{}' com paginação '{}'", turmaId, serviceName, pageable);
        Page<Material> materiaisPage = materialRepository.findByTurmaIdAndAtivoTrue(turmaId, pageable);

        if (materiaisPage.isEmpty()) {
            log.warn("Nenhum material encontrado para Turma ID {}", turmaId);
            return Page.empty();
        }

        // Recupera as URLs assinadas via MinIO
        List<MaterialResponse> minioMateriais = webClient.get()
                .uri(MINIO_BASE_URL + "/lists/" + serviceName + "/" + turmaId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> {
                    log.error("Erro ao buscar materiais no MinIO. Código: {}", response.statusCode().value());
                    return Mono.error(new RuntimeException("Erro ao buscar materiais no MinIO."));
                })
                .bodyToFlux(MaterialResponse.class)
                .collectList()
                .block();
        log.info("Resposta do MinIO para listagem: {} itens", minioMateriais != null ? minioMateriais.size() : 0);

        List<MaterialResponse> minioMateriaisFinal = (minioMateriais != null) ? minioMateriais : List.of();
        List<MaterialResponse> materialResponses = materiaisPage.getContent().stream()
                .map(material -> {
                    String urlArquivo = minioMateriaisFinal.stream()
                            .filter(minio -> minio.nomeArquivo().equals(material.getArquivoId()))
                            .map(MaterialResponse::urlArquivo)
                            .findFirst()
                            .orElse(null);
                    log.debug("Para material ID {} - arquivoId: {}, URL: {}", material.getId(), material.getArquivoId(), urlArquivo);
                    return new MaterialResponse(
                            material.getId(),
                            material.getArquivoId(),
                            urlArquivo,
                            material.getTurma().getId(),
                            material.getProfessor().getId()
                    );
                })
                .collect(Collectors.toList());

        log.info("Listagem concluída. {} materiais retornados.", materialResponses.size());
        return new PageImpl<>(materialResponses, pageable, materiaisPage.getTotalElements());
    }

    @Override
    public MaterialResponse buscarMaterialPorId(Long id) {
        log.info("Buscando material com ID: {}", id);
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material não encontrado"));
        log.info("Material encontrado: {}", material.getId());

        // Chamada GET para obter a URL assinada, aceitando qualquer Content-Type
        String mediaUrl = webClient.get()
                .uri(MINIO_BASE_URL + "/get/" + material.getArquivoId())
                .accept(MediaType.ALL)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> {
                    log.error("Erro ao buscar URL assinada para material ID {} no MinIO. Código: {}",
                            id, response.statusCode().value());
                    return Mono.error(new RuntimeException("Erro ao buscar material no MinIO."));
                })
                .bodyToMono(String.class)
                .block();
        log.info("URL assinada obtida: {}", mediaUrl);

        return new MaterialResponse(
                material.getId(),
                material.getArquivoId(),
                mediaUrl,
                material.getTurma().getId(),
                material.getProfessor().getId()
        );
    }

    @Override
    public MaterialResponse atualizarMaterial(Long id, MaterialRequest materialRequest, byte[] file) {
        log.info("Atualizando material com ID: {}", id);
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material não encontrado"));

        String updatedArquivoId = webClient.put()
                .uri(MINIO_BASE_URL + "/update/" + material.getArquivoId())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .bodyValue(file)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> {
                    log.error("Erro ao atualizar arquivo no MinIO. Código: {}", response.statusCode().value());
                    return Mono.error(new RuntimeException("Erro ao atualizar material no MinIO."));
                })
                .bodyToMono(String.class)
                .block();

        if (updatedArquivoId == null) {
            log.error("Updated arquivoId nulo após chamada ao MinIO");
            throw new RuntimeException("Erro ao atualizar arquivo no MinIO.");
        }

        material.setArquivoId(updatedArquivoId);
        materialRepository.save(material);
        log.info("Material atualizado com sucesso. Novo arquivoId: {}", updatedArquivoId);
        return materialMapper.toResponse(material);
    }

    @Override
    public void deletarMaterial(Long id) {
        log.info("Deletando material com ID: {}", id);
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material não encontrado"));
        webClient.delete()
                .uri(MINIO_BASE_URL + "/delete/" + material.getArquivoId())
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> {
                    log.error("Erro ao remover arquivo do MinIO. Código: {}", response.statusCode().value());
                    return Mono.error(new RuntimeException("Erro ao remover material do MinIO."));
                })
                .toBodilessEntity()
                .block();
        material.setAtivo(false);
        materialRepository.save(material);
        log.info("Material deletado com sucesso. ID: {}", id);
    }

    @Override
    public void associarMaterialAoMural(Long materialId, br.com.ufpb.GerenciadorEscolar.model.Mural mural) {
        log.info("Associando material ID {} ao mural ID {}", materialId, mural.getId());
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Material não encontrado para associação"));
        material.setMural(mural);
        materialRepository.save(material);
        log.info("Material ID {} associado ao mural ID {}", materialId, mural.getId());
    }

}
