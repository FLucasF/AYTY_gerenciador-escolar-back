package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.dto.material.MaterialRequest;
import br.com.ufpb.GerenciadorEscolar.dto.material.MaterialResponse;
import br.com.ufpb.GerenciadorEscolar.service.MaterialServiceInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/materiais")
public class MaterialController {

    private final MaterialServiceInterface materialService;

    public MaterialController(MaterialServiceInterface materialService) {
        this.materialService = materialService;
    }

    @PostMapping
    public ResponseEntity<MaterialResponse> enviarMaterial(@RequestBody MaterialRequest materialRequest) {
        MaterialResponse novoMaterial = materialService.enviarMaterial(materialRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoMaterial);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialResponse> buscarMaterialPorId(@PathVariable Long id) {
        Optional<MaterialResponse> material = materialService.buscarMaterialPorId(id);
        return material.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/turma/{idTurma}")
    public ResponseEntity<List<MaterialResponse>> listarMateriaisPorTurma(@PathVariable Long idTurma) {
        return ResponseEntity.ok(materialService.listarMateriaisPorTurma(idTurma));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarMaterial(@PathVariable Long id) {
        materialService.deletarMaterial(id);
        return ResponseEntity.noContent().build();
    }
}
