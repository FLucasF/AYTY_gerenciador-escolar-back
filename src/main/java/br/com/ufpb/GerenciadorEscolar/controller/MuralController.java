package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.dto.mural.MuralRequest;
import br.com.ufpb.GerenciadorEscolar.dto.mural.MuralResponse;
import br.com.ufpb.GerenciadorEscolar.service.MuralServiceInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/mural")
@Validated
public class MuralController {

    private final MuralServiceInterface muralService;

    public MuralController(MuralServiceInterface muralService) {
        this.muralService = muralService;
    }

    @PostMapping
    public ResponseEntity<MuralResponse> criarPostagem(@RequestBody MuralRequest muralRequest) {
        MuralResponse novaPostagem = muralService.criarPostagem(muralRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaPostagem);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MuralResponse> buscarPostagemPorId(@PathVariable Long id) {
        Optional<MuralResponse> postagem = muralService.buscarPostagemPorId(id);
        return postagem.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/turma/{idTurma}")
    public ResponseEntity<List<MuralResponse>> listarPostagensPorTurma(@PathVariable Long idTurma) {
        return ResponseEntity.ok(muralService.listarPostagensPorTurma(idTurma));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPostagem(@PathVariable Long id) {
        muralService.deletarPostagem(id);
        return ResponseEntity.noContent().build();
    }
}
