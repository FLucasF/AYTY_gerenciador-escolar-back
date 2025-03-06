package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoRequest;
import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoResponse;
import br.com.ufpb.GerenciadorEscolar.service.AlunoServiceInterface;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alunos")
@Validated
public class AlunoController {

    private final AlunoServiceInterface alunoService;

    public AlunoController(AlunoServiceInterface alunoService) {
        this.alunoService = alunoService;
    }

    @PostMapping
    public ResponseEntity<AlunoResponse> cadastrarAluno(@RequestBody @Valid AlunoRequest alunoRequest) {
        AlunoResponse novoAluno = alunoService.cadastrarAluno(alunoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoAluno);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlunoResponse> buscarAlunoPorId(@PathVariable @Min(1) Long id) {
        AlunoResponse alunoResponse = alunoService.buscarAlunoPorId(id);
        return ResponseEntity.ok(alunoResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlunoResponse> atualizarAluno(
            @PathVariable @Min(1) Long id,
            @RequestBody @Valid AlunoRequest alunoRequest) {

        AlunoResponse alunoAtualizado = alunoService.atualizarAluno(id, alunoRequest);
        return ResponseEntity.ok(alunoAtualizado);
    }

    @GetMapping
    public ResponseEntity<Page<AlunoResponse>> listarAlunos(Pageable pageable) {
        Page<AlunoResponse> alunosPage = alunoService.listarAlunosAtivos(pageable);
        return ResponseEntity.ok(alunosPage);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativarAluno(@PathVariable @Min(1) Long id) {
        alunoService.desativarAluno(id);
        return ResponseEntity.noContent().build();
    }
}
