package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorRequest;
import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorResponse;
import br.com.ufpb.GerenciadorEscolar.service.AdministradorServiceInterface;
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

    @PostMapping
    public ResponseEntity<AdministradorResponse> cadastrarAdministrador(@RequestBody @Valid AdministradorRequest administradorRequest) {
        AdministradorResponse novoAdmin = administradorService.cadastrarAdministrador(administradorRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoAdmin);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdministradorResponse> buscarAdministradorPorId(@PathVariable @Min(1) Long id) {
        AdministradorResponse adminResponse = administradorService.buscarAdministradorPorId(id);
        return ResponseEntity.ok(adminResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdministradorResponse> atualizarAdministrador(
            @PathVariable @Min(1) Long id,
            @RequestBody @Valid AdministradorRequest administradorRequest) {

        AdministradorResponse administradorAtualizado = administradorService.atualizarAdministrador(id, administradorRequest);
        return ResponseEntity.ok(administradorAtualizado);
    }

    @GetMapping
    public ResponseEntity<Page<AdministradorResponse>> listarAdministradores(Pageable pageable) {
        Page<AdministradorResponse> adminPage = administradorService.listarAdministradoresAtivos(pageable);
        return ResponseEntity.ok(adminPage);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativarAdministrador(@PathVariable @Min(1) Long id) {
        administradorService.desativarAdministrador(id);
        return ResponseEntity.noContent().build();
    }
}
