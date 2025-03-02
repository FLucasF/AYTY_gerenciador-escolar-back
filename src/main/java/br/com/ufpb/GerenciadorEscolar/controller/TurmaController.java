package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoResponse;
import br.com.ufpb.GerenciadorEscolar.dto.turma.TurmaRequest;
import br.com.ufpb.GerenciadorEscolar.dto.turma.TurmaResponse;
import br.com.ufpb.GerenciadorEscolar.service.TurmaServiceImpl;
import br.com.ufpb.GerenciadorEscolar.service.TurmaServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger logger = LoggerFactory.getLogger(TurmaServiceImpl.class);  // Logger

    public TurmaController(TurmaServiceInterface turmaService) {
        this.turmaService = turmaService;
    }

    @GetMapping("/geral")
    public ResponseEntity<Page<TurmaResponse>> listarTodasAsTurmas(Pageable pageable) {
        Page<TurmaResponse> turmas = turmaService.listarTodasTurmas(pageable);
        return ResponseEntity.ok(turmas);
    }

    @GetMapping("/professor/{professorId}")
    public ResponseEntity<Page<TurmaResponse>> listarTurmasParaProfessor(@PathVariable Long professorId, Pageable pageable) {
        Page<TurmaResponse> turmas = turmaService.listarTurmasPorProfessor(professorId, pageable);
        logger.info("✅ Turmas encontradas para o professor ID " + professorId + ": " + turmas.getContent());
        return ResponseEntity.ok(turmas);
    }

    @GetMapping("/aluno/{alunoId}")
    public ResponseEntity<Page<TurmaResponse>> listarTurmasParaAluno(@PathVariable Long alunoId, Pageable pageable) {
        logger.info("📜 Buscando turmas para o aluno com ID: " + alunoId);

        // Buscando as turmas associadas ao aluno
        Page<TurmaResponse> turmas = turmaService.listarTurmasPorAluno(alunoId, pageable);

        // Logando as turmas antes de retornar
        logger.info("✅ Turmas encontradas para o aluno ID " + alunoId + ": " + turmas.getContent());

        return ResponseEntity.ok(turmas);
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

    @DeleteMapping("/{turmaId}/remover/{alunoId}")
    public ResponseEntity<Page<AlunoResponse>> removerAlunoDaTurma(@PathVariable Long turmaId, @PathVariable Long alunoId) {
        Page<AlunoResponse> alunosAtualizados = turmaService.removerAlunoDaTurma(turmaId, alunoId);
        return ResponseEntity.ok(alunosAtualizados);
    }

    @GetMapping("/{turmaId}/alunos")
    public ResponseEntity<Page<AlunoResponse>> listarAlunosPorTurma(@PathVariable Long turmaId, Pageable pageable) {
        return ResponseEntity.ok(turmaService.listarAlunosPorTurma(turmaId, pageable));
    }
}
