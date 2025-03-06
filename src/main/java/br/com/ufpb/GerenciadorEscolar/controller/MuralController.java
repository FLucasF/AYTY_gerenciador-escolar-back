package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.dto.mural.MuralRequest;
import br.com.ufpb.GerenciadorEscolar.dto.mural.MuralResponse;
import br.com.ufpb.GerenciadorEscolar.service.MuralServiceInterface;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/murais")
@Validated
public class MuralController {

    private final MuralServiceInterface muralService;

    public MuralController(MuralServiceInterface muralService) {
        this.muralService = muralService;
    }

    @PostMapping
    public ResponseEntity<MuralResponse> criarPostagem(@RequestBody @Valid MuralRequest muralRequest) {
        MuralResponse novaPostagem = muralService.criarPostagem(muralRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaPostagem);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MuralResponse> buscarPostagemPorId(@PathVariable @Min(1) Long id) {
        MuralResponse postagem = muralService.buscarPostagemPorId(id);
        return ResponseEntity.ok(postagem);
    }

    @GetMapping("/turmas/{idTurma}")
    public Page<MuralResponse> listarPostagensPorTurma(@PathVariable @Min(1) Long idTurma, Pageable pageable) {
        return muralService.listarPostagensPorTurma(idTurma, pageable);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPostagem(@PathVariable @Min(1) Long id) {
        muralService.deletarPostagem(id);
        return ResponseEntity.noContent().build();
    }
}
