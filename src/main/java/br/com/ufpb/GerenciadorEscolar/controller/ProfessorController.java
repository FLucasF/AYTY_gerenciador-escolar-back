package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorRequest;
import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorResponse;
import br.com.ufpb.GerenciadorEscolar.service.ProfessorServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/professores")
public class ProfessorController {

    private final ProfessorServiceImpl professorService;

    public ProfessorController(ProfessorServiceImpl professorService) {
        this.professorService = professorService;
    }

    @PostMapping
    public ResponseEntity<ProfessorResponse> cadastrarProfessor(@RequestBody ProfessorRequest professorRequest) {
        ProfessorResponse novoProfessor = professorService.cadastrarProfessor(professorRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoProfessor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfessorResponse> atualizarProfessor(@PathVariable Long id, @RequestBody ProfessorRequest professorRequest) {
        ProfessorResponse professorAtualizado = professorService.atualizarProfessor(id, professorRequest);
        return ResponseEntity.ok(professorAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativarProfessor(@PathVariable Long id) {
        professorService.desativarProfessor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<ProfessorResponse>> listarProfessores(Pageable pageable) {
        Page<ProfessorResponse> professoresPage = professorService.listarProfessoresAtivos(pageable);
        return ResponseEntity.ok(professoresPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfessorResponse> buscarProfessorPorId(@PathVariable Long id) {
        Optional<ProfessorResponse> professorResponse = professorService.buscarProfessorPorId(id);
        return professorResponse.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
