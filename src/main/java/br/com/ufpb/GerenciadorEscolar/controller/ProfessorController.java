package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorRequest;
import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorResponse;
import br.com.ufpb.GerenciadorEscolar.dto.ApiError;
import br.com.ufpb.GerenciadorEscolar.service.ProfessorServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/professores")
@Validated
public class ProfessorController {

    private final ProfessorServiceInterface professorService;

    public ProfessorController(ProfessorServiceInterface professorService) {
        this.professorService = professorService;
    }

    @Operation(summary = "Cadastra um novo professor",
            description = "Cria um novo professor na aplicação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Professor cadastrado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProfessorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou conflito (ex.: email, CPF ou SIAPE já cadastrado)",
                    content = @Content(mediaType = "application/problem+json",
                            schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    public ResponseEntity<ProfessorResponse> cadastrarProfessor(@RequestBody @Valid ProfessorRequest professorRequest) {
        ProfessorResponse novoProfessor = professorService.cadastrarProfessor(professorRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoProfessor);
    }

    @Operation(summary = "Busca professor por ID",
            description = "Recupera os detalhes de um professor através do seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Professor encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProfessorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Professor não encontrado",
                    content = @Content(mediaType = "application/problem+json",
                            schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProfessorResponse> buscarProfessorPorId(@PathVariable @Min(1) Long id) {
        ProfessorResponse professorResponse = professorService.buscarProfessorPorId(id);
        return ResponseEntity.ok(professorResponse);
    }

    @Operation(summary = "Atualiza um professor",
            description = "Atualiza os dados de um professor existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Professor atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProfessorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou nenhuma alteração realizada",
                    content = @Content(mediaType = "application/problem+json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Professor não encontrado",
                    content = @Content(mediaType = "application/problem+json",
                            schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProfessorResponse> atualizarProfessor(
            @PathVariable @Min(1) Long id,
            @RequestBody @Valid ProfessorRequest professorRequest) {
        ProfessorResponse professorAtualizado = professorService.atualizarProfessor(id, professorRequest);
        return ResponseEntity.ok(professorAtualizado);
    }

    @Operation(summary = "Lista professores",
            description = "Lista os professores ativos com paginação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Professores listados com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProfessorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos",
                    content = @Content(mediaType = "application/problem+json",
                            schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping
    public ResponseEntity<Page<ProfessorResponse>> listarProfessores(Pageable pageable) {
        Page<ProfessorResponse> professoresPage = professorService.listarProfessoresAtivos(pageable);
        return ResponseEntity.ok(professoresPage);
    }

    @Operation(summary = "Desativa um professor",
            description = "Desativa (logicamente) um professor através do seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Professor desativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Professor não encontrado",
                    content = @Content(mediaType = "application/problem+json",
                            schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativarProfessor(@PathVariable @Min(1) Long id) {
        professorService.desativarProfessor(id);
        return ResponseEntity.noContent().build();
    }
}
