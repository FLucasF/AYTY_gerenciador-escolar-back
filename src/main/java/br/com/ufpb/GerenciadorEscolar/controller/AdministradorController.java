package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorRequest;
import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorResponse;
import br.com.ufpb.GerenciadorEscolar.service.AdministradorServiceInterface;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/administradores")
public class AdministradorController {

    private final AdministradorServiceInterface administradorService;

    public AdministradorController(AdministradorServiceInterface administradorService) {
        this.administradorService = administradorService;
    }

    @PostMapping
    public ResponseEntity<AdministradorResponse> cadastrarAdministrador(@RequestBody AdministradorRequest administradorRequest) {
        AdministradorResponse novoAdmin = administradorService.cadastrarAdministrador(administradorRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoAdmin);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativarAdministrador(@PathVariable Long id) {
        administradorService.desativarAdministrador(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<AdministradorResponse>> listarAdministradores(Pageable pageable) {
        Page<AdministradorResponse> adminPage = administradorService.listarAdministradoresAtivos(pageable);
        return ResponseEntity.ok(adminPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdministradorResponse> buscarAdministradorPorId(@PathVariable Long id) {
        Optional<AdministradorResponse> adminResponse = administradorService.buscarAdministradorPorId(id);
        return adminResponse.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}

