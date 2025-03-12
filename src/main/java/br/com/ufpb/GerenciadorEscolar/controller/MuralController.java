package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.model.dto.mural.MuralRequest;
import br.com.ufpb.GerenciadorEscolar.model.dto.mural.MuralResponse;
import br.com.ufpb.GerenciadorEscolar.model.dto.ApiError;
import br.com.ufpb.GerenciadorEscolar.service.MuralServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/murais")
@Slf4j
public class MuralController {

    private final MuralServiceImpl muralService;

    @Autowired
    public MuralController(MuralServiceImpl muralService) {
        this.muralService = muralService;
    }

    @Operation(
            summary = "Cria uma nova postagem no mural",
            description = "Cria uma postagem no mural para uma turma e opcionalmente carrega uma imagem associada."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Postagem criada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MuralResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou erro no processamento da imagem",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MuralResponse> criarPostagem(
            @Parameter(description = "Dados da postagem no mural", required = true)
            @RequestPart("mural") MuralRequest muralRequest,
            @Parameter(description = "Imagem opcional para a postagem")
            @RequestPart(value = "imagem", required = false) MultipartFile imagem) {

        if (imagem != null) {
            log.info("Imagem recebida: {} - Tamanho: {} bytes",
                    imagem.getOriginalFilename(), imagem.getSize());
        } else {
            log.info("Imagem está nula");
        }
        MuralResponse response = muralService.criarPostagem(muralRequest, imagem);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Lista postagens do mural por turma",
            description = "Lista as postagens do mural de uma turma específica com paginação."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Postagens listadas com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MuralResponse.class))),
            @ApiResponse(responseCode = "204", description = "Nenhuma postagem encontrada para a turma"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/turma/{idTurma}")
    public ResponseEntity<Page<MuralResponse>> listarPostagensPorTurma(
            @Parameter(description = "ID da turma", required = true) @PathVariable("idTurma") Long idTurma,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "dataCriacao,desc") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("dataCriacao")));
        log.info("Listando postagens da turma ID: {} com paginação: {}", idTurma, pageable);
        Page<MuralResponse> response = muralService.listarPostagensPorTurma(idTurma, pageable);

        if (response.isEmpty()) {
            log.warn("Nenhuma postagem encontrada para a turma ID: {}", idTurma);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Busca uma postagem do mural por ID",
            description = "Retorna os detalhes de uma postagem do mural a partir do seu ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Postagem encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MuralResponse.class))),
            @ApiResponse(responseCode = "404", description = "Postagem não encontrada",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<MuralResponse> buscarPostagemPorId(
            @Parameter(description = "ID da postagem", required = true) @PathVariable Long id) {
        MuralResponse response = muralService.buscarPostagemPorId(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Deleta (desativa) uma postagem do mural",
            description = "Desativa (deleta logicamente) uma postagem do mural a partir do seu ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Postagem desativada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Postagem não encontrada",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPostagem(
            @Parameter(description = "ID da postagem a ser deletada", required = true) @PathVariable Long id) {
        log.info("Recebendo solicitação para deletar material com ID: {}", id);
        muralService.deletarPostagem(id);
        log.info("Material deletado com sucesso. ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
