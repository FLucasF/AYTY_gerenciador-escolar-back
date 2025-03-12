package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoResponse;
import br.com.ufpb.GerenciadorEscolar.dto.turma.TurmaRequest;
import br.com.ufpb.GerenciadorEscolar.dto.turma.TurmaResponse;
import br.com.ufpb.GerenciadorEscolar.dto.ApiError;
import br.com.ufpb.GerenciadorEscolar.service.TurmaServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/turmas")
@Slf4j
@Validated
public class TurmaController {

    private final TurmaServiceInterface turmaService;
    private final Logger logger = LoggerFactory.getLogger(TurmaController.class);

    public TurmaController(TurmaServiceInterface turmaService) {
        this.turmaService = turmaService;
    }

    @Operation(
            summary = "Lista todas as turmas",
            description = "Retorna todas as turmas cadastradas com paginação."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Turmas listadas com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TurmaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/geral")
    public ResponseEntity<Page<TurmaResponse>> listarTodasAsTurmas(Pageable pageable) {
        Page<TurmaResponse> turmas = turmaService.listarTodasTurmas(pageable);
        return ResponseEntity.ok(turmas);
    }

    @Operation(
            summary = "Lista turmas para um professor",
            description = "Retorna as turmas associadas a um professor específico com paginação."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Turmas listadas com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TurmaResponse.class))),
            @ApiResponse(responseCode = "400", description = "ID do professor inválido ou parâmetros incorretos",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/professor/{professorId}")
    public ResponseEntity<Page<TurmaResponse>> listarTurmasParaProfessor(
            @Parameter(description = "ID do professor", required = true) @PathVariable Long professorId,
            Pageable pageable) {
        Page<TurmaResponse> turmas = turmaService.listarTurmasPorProfessor(professorId, pageable);
        logger.info("Turmas encontradas para o professor ID {}: {}", professorId, turmas.getContent());
        return ResponseEntity.ok(turmas);
    }

    @Operation(
            summary = "Lista turmas para um aluno",
            description = "Retorna as turmas em que um aluno está matriculado, com paginação."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Turmas listadas com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TurmaResponse.class))),
            @ApiResponse(responseCode = "400", description = "ID do aluno inválido ou parâmetros incorretos",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/aluno/{alunoId}")
    public ResponseEntity<Page<TurmaResponse>> listarTurmasParaAluno(
            @Parameter(description = "ID do aluno", required = true) @PathVariable Long alunoId,
            Pageable pageable) {
        logger.info("Buscando turmas para o aluno com ID: {}", alunoId);
        Page<TurmaResponse> turmas = turmaService.listarTurmasPorAluno(alunoId, pageable);
        logger.info("Turmas encontradas para o aluno ID {}: {}", alunoId, turmas.getContent());
        return ResponseEntity.ok(turmas);
    }

    @Operation(
            summary = "Cria uma nova turma",
            description = "Cria uma nova turma com os dados informados."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Turma criada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TurmaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    public ResponseEntity<TurmaResponse> criarTurma(@RequestBody TurmaRequest turmaRequest) {
        TurmaResponse novaTurma = turmaService.criarTurma(turmaRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaTurma);
    }

    @Operation(
            summary = "Atualiza uma turma",
            description = "Atualiza os dados de uma turma existente com base no ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Turma atualizada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TurmaResponse.class))),
            @ApiResponse(responseCode = "404", description = "Turma não encontrada",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<TurmaResponse> atualizarTurma(
            @Parameter(description = "ID da turma a ser atualizada", required = true) @PathVariable Long id,
            @RequestBody TurmaRequest turmaRequest) {
        TurmaResponse turmaAtualizada = turmaService.atualizarTurma(id, turmaRequest);
        return ResponseEntity.ok(turmaAtualizada);
    }

    @Operation(
            summary = "Busca uma turma por ID",
            description = "Retorna os detalhes de uma turma a partir do seu ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Turma encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TurmaResponse.class))),
            @ApiResponse(responseCode = "404", description = "Turma não encontrada",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<TurmaResponse> buscarTurmaPorId(@PathVariable Long id) {
        Optional<TurmaResponse> turma = turmaService.buscarTurmaPorId(id);
        return turma.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Deleta uma turma",
            description = "Remove uma turma a partir do seu ID. A operação é lógica, desativando a turma."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Turma deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Turma não encontrada",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTurma(@PathVariable Long id) {
        turmaService.deletarTurma(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Matricula um aluno em uma turma",
            description = "Adiciona um aluno a uma turma e retorna a turma atualizada."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aluno matriculado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TurmaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou matrícula não permitida",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/{turmaId}/matricular/{alunoId}")
    public ResponseEntity<TurmaResponse> matricularAluno(
            @Parameter(description = "ID da turma", required = true) @PathVariable Long turmaId,
            @Parameter(description = "ID do aluno", required = true) @PathVariable Long alunoId) {
        TurmaResponse turmaAtualizada = turmaService.matricularAluno(turmaId, alunoId);
        return ResponseEntity.ok(turmaAtualizada);
    }

    @Operation(
            summary = "Remove um aluno de uma turma",
            description = "Remove um aluno matriculado em uma turma."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Aluno removido da turma com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou aluno não matriculado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/{turmaId}/remover/{alunoId}")
    public ResponseEntity<Void> removerAlunoDaTurma(
            @Parameter(description = "ID da turma", required = true) @PathVariable Long turmaId,
            @Parameter(description = "ID do aluno", required = true) @PathVariable Long alunoId) {
        turmaService.removerAlunoDaTurma(turmaId, alunoId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Lista alunos de uma turma",
            description = "Retorna a lista de alunos matriculados em uma turma com paginação."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alunos listados com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlunoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{turmaId}/alunos")
    public ResponseEntity<Page<AlunoResponse>> listarAlunosPorTurma(
            @Parameter(description = "ID da turma", required = true) @PathVariable Long turmaId,
            Pageable pageable) {
        return ResponseEntity.ok(turmaService.listarAlunosPorTurma(turmaId, pageable));
    }
}
