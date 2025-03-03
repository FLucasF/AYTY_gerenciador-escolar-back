package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoRequest;
import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoResponse;
import br.com.ufpb.GerenciadorEscolar.service.AlunoServiceInterface;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/alunos")
@Validated
public class AlunoController {

    private final AlunoServiceInterface alunoService;

    public AlunoController(AlunoServiceInterface alunoService) {
        this.alunoService = alunoService;
    }

    @PostMapping
    public ResponseEntity<AlunoResponse> cadastrarAluno(@RequestBody AlunoRequest alunoRequest) {
        AlunoResponse novoAluno = alunoService.cadastrarAluno(alunoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoAluno);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlunoResponse> atualizarAluno(@PathVariable Long id, @RequestBody AlunoRequest alunoRequest) {
        AlunoResponse alunoAtualizado = alunoService.atualizarAluno(id, alunoRequest);
        return ResponseEntity.ok(alunoAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativarAluno(@PathVariable Long id) {
        alunoService.desativarAluno(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<AlunoResponse>> listarAlunos(Pageable pageable) {
        Page<AlunoResponse> alunosPage = alunoService.listarAlunosAtivos(pageable);
        return ResponseEntity.ok(alunosPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlunoResponse> buscarAlunoPorId(@PathVariable Long id) {
        Optional<AlunoResponse> alunoResponse = alunoService.buscarAlunoPorId(id);
        return alunoResponse.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
