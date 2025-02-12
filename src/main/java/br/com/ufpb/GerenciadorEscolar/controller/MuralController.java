package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.model.Mural;
import br.com.ufpb.GerenciadorEscolar.service.MuralServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mural")
public class MuralController {

    @Autowired
    private MuralServiceInterface muralService;

    @PostMapping
    public ResponseEntity<Mural> criarPostagem(@RequestBody Mural mural) {
        Mural novaPostagem = muralService.criarPostagem(mural);
        return ResponseEntity.ok(novaPostagem);
    }

    @GetMapping("/turma/{idTurma}")
    public ResponseEntity<List<Mural>> listarPostagensPorTurma(@PathVariable Long idTurma) {
        return ResponseEntity.ok(muralService.listarPostagensPorTurma(idTurma));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPostagem(@PathVariable Long id) {
        muralService.deletarPostagem(id);
        return ResponseEntity.noContent().build();
    }
}
