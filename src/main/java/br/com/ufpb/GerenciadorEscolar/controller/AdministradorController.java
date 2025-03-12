package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.model.dto.administrador.AdministradorRequest;
import br.com.ufpb.GerenciadorEscolar.model.dto.administrador.AdministradorResponse;
import br.com.ufpb.GerenciadorEscolar.model.dto.ApiError;
import br.com.ufpb.GerenciadorEscolar.service.AdministradorServiceInterface;
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
@RequestMapping("/administradores")
@Validated
public class AdministradorController {

    private final AdministradorServiceInterface administradorService;

    public AdministradorController(AdministradorServiceInterface administradorService) {
        this.administradorService = administradorService;
    }

    @Operation(
            summary = "Cadastra um novo administrador",
            description = "Cria um novo administrador com os dados informados."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Administrador criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou conflito (ex.: email, CPF ou SIAPE já cadastrados)",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    public ResponseEntity<AdministradorResponse> cadastrarAdministrador(
            @RequestBody @Valid AdministradorRequest administradorRequest) {
        AdministradorResponse novoAdmin = administradorService.cadastrarAdministrador(administradorRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoAdmin);
    }

    @Operation(
            summary = "Busca um administrador por ID",
            description = "Recupera os dados de um administrador a partir do ID informado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Administrador encontrado"),
            @ApiResponse(responseCode = "404", description = "Administrador não encontrado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<AdministradorResponse> buscarAdministradorPorId(
            @Parameter(description = "ID do administrador", required = true, example = "1")
            @PathVariable @Min(1) Long id) {
        AdministradorResponse adminResponse = administradorService.buscarAdministradorPorId(id);
        return ResponseEntity.ok(adminResponse);
    }

    @Operation(
            summary = "Lista administradores ativos com paginação",
            description = "Retorna uma página com os administradores ativos."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Administradores listados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping
    public ResponseEntity<Page<AdministradorResponse>> listarAdministradores(
            @Parameter(hidden = true) Pageable pageable) {
        Page<AdministradorResponse> adminPage = administradorService.listarAdministradoresAtivos(pageable);
        return ResponseEntity.ok(adminPage);
    }

    @Operation(
            summary = "Atualiza um administrador existente",
            description = "Atualiza os dados de um administrador a partir do ID informado. Retorna 404 se o administrador ou o login não forem encontrados, e 400 se nenhum dado for alterado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Administrador atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdministradorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou nenhuma alteração realizada",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Administrador ou login não encontrado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<AdministradorResponse> atualizarAdministrador(
            @Parameter(description = "ID do administrador a ser atualizado", required = true, example = "1")
            @PathVariable @Min(1) Long id,
            @RequestBody @Valid AdministradorRequest administradorRequest) {
        AdministradorResponse administradorAtualizado = administradorService.atualizarAdministrador(id, administradorRequest);
        return ResponseEntity.ok(administradorAtualizado);
    }

    @Operation(
            summary = "Desativa um administrador existente",
            description = "Desativa (remoção lógica) o administrador a partir do ID informado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Administrador desativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Administrador não encontrado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativarAdministrador(
            @Parameter(description = "ID do administrador", required = true, example = "1")
            @PathVariable @Min(1) Long id) {
        administradorService.desativarAdministrador(id);
        return ResponseEntity.noContent().build();
    }
}
