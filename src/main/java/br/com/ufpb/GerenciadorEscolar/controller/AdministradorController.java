package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.dto.AdministradorDTO;
import br.com.ufpb.GerenciadorEscolar.model.Administrador;
import br.com.ufpb.GerenciadorEscolar.service.AdministradorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/administradores")
@CrossOrigin("*")
public class AdministradorController {

    @Autowired
    private AdministradorServiceImpl administradorService;

    /**
     * Listar todos os administradores ativos.
     */
    @GetMapping
    public ResponseEntity<List<AdministradorDTO>> listarTodosAdministradores() {
        List<AdministradorDTO> administradoresDTO = administradorService.listarAdministradoresAtivos()
                .stream()
                .map(admin -> new AdministradorDTO(
                        admin.getId(),
                        admin.getNome(),
                        admin.getEmail(),
                        admin.getSetor()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(administradoresDTO);
    }

    /**
     * Buscar um administrador por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AdministradorDTO> buscarAdministradorPorId(@PathVariable Long id) {
        return administradorService.buscarAdministradorPorId(id)
                .map(admin -> ResponseEntity.ok(new AdministradorDTO(
                        admin.getId(),
                        admin.getNome(),
                        admin.getEmail(),
                        admin.getSetor()
                )))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Cadastrar um novo administrador.
     */
    @PostMapping
    public ResponseEntity<AdministradorDTO> cadastrarAdministrador(@RequestBody Administrador administrador) {
        Administrador novoAdministrador = administradorService.cadastrarAdministrador(administrador);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AdministradorDTO(
                        novoAdministrador.getId(),
                        novoAdministrador.getNome(),
                        novoAdministrador.getEmail(),
                        novoAdministrador.getSetor()
                ));
    }

    /**
     * Atualizar os dados de um administrador existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AdministradorDTO> atualizarAdministrador(@PathVariable Long id, @RequestBody Administrador administrador) {
        Administrador administradorAtualizado = administradorService.atualizarAdministrador(id, administrador);
        return ResponseEntity.ok(new AdministradorDTO(
                administradorAtualizado.getId(),
                administradorAtualizado.getNome(),
                administradorAtualizado.getEmail(),
                administradorAtualizado.getSetor()
        ));
    }

    /**
     * Desativar um administrador.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> desativarAdministrador(@PathVariable Long id) {
        administradorService.desativarAdministrador(id);
        return ResponseEntity.ok("Administrador com ID " + id + " foi desativado com sucesso.");
    }
}
