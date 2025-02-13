package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.model.Material;
import br.com.ufpb.GerenciadorEscolar.service.interfaces.MaterialServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/materiais")
public class MaterialController {

    @Autowired
    private MaterialServiceInterface materialService;

    @PostMapping
    public ResponseEntity<Material> enviarMaterial(@RequestBody Material material) {
        Material novoMaterial = materialService.enviarMaterial(material);
        return ResponseEntity.ok(novoMaterial);
    }

    @GetMapping("/turma/{idTurma}")
    public ResponseEntity<List<Material>> listarMateriaisPorTurma(@PathVariable Long idTurma) {
        return ResponseEntity.ok(materialService.listarMateriaisPorTurma(idTurma));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarMaterial(@PathVariable Long id) {
        materialService.deletarMaterial(id);
        return ResponseEntity.noContent().build();
    }
}
