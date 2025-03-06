package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorRequest;
import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorResponse;
import br.com.ufpb.GerenciadorEscolar.service.ProfessorServiceInterface;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/professores")
@Validated
public class ProfessorController {

    private final ProfessorServiceInterface professorService;

    public ProfessorController(ProfessorServiceInterface professorService) {
        this.professorService = professorService;
    }

    @PostMapping
    public ResponseEntity<ProfessorResponse> cadastrarProfessor(@RequestBody @Valid ProfessorRequest professorRequest) {
        ProfessorResponse novoProfessor = professorService.cadastrarProfessor(professorRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoProfessor);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfessorResponse> buscarProfessorPorId(@PathVariable @Min(1) Long id) {
        ProfessorResponse professorResponse = professorService.buscarProfessorPorId(id);
        return ResponseEntity.ok(professorResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfessorResponse> atualizarProfessor(
            @PathVariable @Min(1) Long id,
            @RequestBody @Valid ProfessorRequest professorRequest) {

        ProfessorResponse professorAtualizado = professorService.atualizarProfessor(id, professorRequest);
        return ResponseEntity.ok(professorAtualizado);
    }

    @GetMapping
    public ResponseEntity<Page<ProfessorResponse>> listarProfessores(Pageable pageable) {
        Page<ProfessorResponse> professoresPage = professorService.listarProfessoresAtivos(pageable);
        return ResponseEntity.ok(professoresPage);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativarProfessor(@PathVariable @Min(1) Long id) {
        professorService.desativarProfessor(id);
        return ResponseEntity.noContent().build();
    }
}
