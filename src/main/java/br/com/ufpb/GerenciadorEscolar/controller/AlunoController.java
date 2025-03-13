package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.model.dto.aluno.AlunoRequest;
import br.com.ufpb.GerenciadorEscolar.model.dto.aluno.AlunoResponse;
import br.com.ufpb.GerenciadorEscolar.model.dto.ApiError;
import br.com.ufpb.GerenciadorEscolar.service.AlunoServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/alunos")
@Validated
public class AlunoController {

    private final AlunoServiceInterface alunoService;

    public AlunoController(AlunoServiceInterface alunoService) {
        this.alunoService = alunoService;
    }

    @Operation(
            summary = "Cadastra um novo aluno",
            description = "Cria um novo aluno com os dados informados. Em caso de conflito (ex.: email ou CPF já cadastrado), retorna erro 400."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Aluno criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou conflito (ex.: email ou CPF já cadastrado)",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    public ResponseEntity<AlunoResponse> cadastrarAluno(
            @RequestBody @Valid AlunoRequest alunoRequest) {
        AlunoResponse novoAluno = alunoService.cadastrarAluno(alunoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoAluno);
    }

    @Operation(
            summary = "Busca um aluno por ID",
            description = "Recupera os detalhes de um aluno a partir do ID informado. Retorna erro 404 se o aluno não for encontrado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aluno encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlunoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<AlunoResponse> buscarAlunoPorId(
            @Parameter(description = "ID do aluno", required = true, example = "1")
            @PathVariable @Min(1) Long id) {
        AlunoResponse alunoResponse = alunoService.buscarAlunoPorId(id);
        return ResponseEntity.ok(alunoResponse);
    }

    @Operation(
            summary = "Atualiza um aluno existente",
            description = "Atualiza os dados de um aluno a partir do ID informado. Retorna erro 404 se o aluno ou o login não forem encontrados, e erro 400 se nenhum dado for alterado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aluno atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlunoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou nenhuma alteração realizada",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Aluno ou login não encontrado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<AlunoResponse> atualizarAluno(
            @Parameter(description = "ID do aluno a ser atualizado", required = true, example = "1")
            @PathVariable @Min(1) Long id,
            @RequestBody @Valid AlunoRequest alunoRequest) {
        AlunoResponse alunoAtualizado = alunoService.atualizarAluno(id, alunoRequest);
        return ResponseEntity.ok(alunoAtualizado);
    }

    @Operation(
            summary = "Lista alunos ativos com paginação",
            description = "Retorna uma página de alunos ativos. Os parâmetros de paginação são opcionais e devem ser passados via query string."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alunos listados com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlunoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping
    public ResponseEntity<Page<AlunoResponse>> listarAlunos(
            @Parameter(hidden = true) Pageable pageable) {
        Page<AlunoResponse> alunosPage = alunoService.listarAlunosAtivos(pageable);
        return ResponseEntity.ok(alunosPage);
    }

    @Operation(
            summary = "Desativa um aluno existente",
            description = "Desativa (remoção lógica) um aluno a partir do ID informado. Retorna erro 404 se o aluno não for encontrado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Aluno desativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativarAluno(
            @Parameter(description = "ID do aluno", required = true, example = "1")
            @PathVariable @Min(1) Long id) {
        alunoService.desativarAluno(id);
        return ResponseEntity.noContent().build();
    }
}
