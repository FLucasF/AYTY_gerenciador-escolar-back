package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.dto.turma.TurmaRequest;
import br.com.ufpb.GerenciadorEscolar.dto.turma.TurmaResponse;
import br.com.ufpb.GerenciadorEscolar.service.TurmaServiceInterface;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/turmas")
public class TurmaController {

    private final TurmaServiceInterface turmaService;

    public TurmaController(TurmaServiceInterface turmaService) {
        this.turmaService = turmaService;
    }

    @PostMapping
    public ResponseEntity<TurmaResponse> criarTurma(@RequestBody TurmaRequest turmaRequest) {
        TurmaResponse novaTurma = turmaService.criarTurma(turmaRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaTurma);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TurmaResponse> atualizarTurma(@PathVariable Long id, @RequestBody TurmaRequest turmaRequest) {
        TurmaResponse turmaAtualizada = turmaService.atualizarTurma(id, turmaRequest);
        return ResponseEntity.ok(turmaAtualizada);
    }

    @GetMapping
    public ResponseEntity<Page<TurmaResponse>> listarTodasTurmas(Pageable pageable) {
        return ResponseEntity.ok(turmaService.listarTodasTurmas(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TurmaResponse> buscarTurmaPorId(@PathVariable Long id) {
        Optional<TurmaResponse> turma = turmaService.buscarTurmaPorId(id);
        return turma.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTurma(@PathVariable Long id) {
        turmaService.deletarTurma(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{turmaId}/matricular/{alunoId}")
    public ResponseEntity<TurmaResponse> matricularAluno(@PathVariable Long turmaId, @PathVariable Long alunoId) {
        TurmaResponse turmaAtualizada = turmaService.matricularAluno(turmaId, alunoId);
        return ResponseEntity.ok(turmaAtualizada);
    }

    @GetMapping("/professor/{professorId}")
    public ResponseEntity<Page<TurmaResponse>> listarTurmasPorProfessor(@PathVariable Long professorId, Pageable pageable) {
        return ResponseEntity.ok(turmaService.listarTurmasPorProfessor(professorId, pageable));
    }
}
