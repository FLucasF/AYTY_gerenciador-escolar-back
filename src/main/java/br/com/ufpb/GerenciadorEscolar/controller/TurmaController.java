package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.dto.TurmaDTO;
import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoResponse;
import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.model.Turma;
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
    public ResponseEntity<TurmaDTO> criarTurma(@RequestBody Turma turma) {
        Turma novaTurma = turmaService.criarTurma(turma);
        return ResponseEntity.status(HttpStatus.CREATED).body(new TurmaDTO(novaTurma));
    }

    @PostMapping("/{turmaId}/matricular/{alunoId}")
    public ResponseEntity<TurmaDTO> matricularAluno(@PathVariable Long turmaId, @PathVariable Long alunoId) {
        Turma turma = turmaService.matricularAluno(turmaId, alunoId);
        return ResponseEntity.ok(new TurmaDTO(turma));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TurmaDTO> atualizarTurma(@PathVariable Long id, @RequestBody Turma turmaAtualizada) {
        Turma turma = turmaService.atualizarTurma(id, turmaAtualizada);
        return ResponseEntity.ok(new TurmaDTO(turma));
    }

    @GetMapping
    public ResponseEntity<Page<TurmaDTO>> listarTodasTurmas(Pageable pageable) {
        Page<Turma> turmasPage = turmaService.listarTodasTurmas(pageable);
        Page<TurmaDTO> turmasDTOPage = turmasPage.map(TurmaDTO::new);
        return ResponseEntity.ok(turmasDTOPage);
    }

    @GetMapping("/{turmaId}/alunos")
    public ResponseEntity<Page<AlunoResponse>> listarAlunosPorTurma(@PathVariable Long turmaId, Pageable pageable) {
        Page<Aluno> alunosPage = turmaService.listarAlunosPorTurma(turmaId, pageable);
        Page<AlunoResponse> alunosResponsePage = alunosPage.map(aluno -> new AlunoResponse(
                aluno.getId(), aluno.getNome(), aluno.getEmail(), aluno.getCpf(), aluno.getCurso()
        ));
        return ResponseEntity.ok(alunosResponsePage);
    }


    @GetMapping("/{id}")
    public ResponseEntity<TurmaDTO> buscarTurmaPorId(@PathVariable Long id) {
        Optional<Turma> turma = turmaService.buscarTurmaPorId(id);
        return turma.map(value -> ResponseEntity.ok(new TurmaDTO(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTurma(@PathVariable Long id) {
        turmaService.deletarTurma(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{turmaId}/remover/{alunoId}")
    public ResponseEntity<TurmaDTO> removerAlunoDaTurma(@PathVariable Long turmaId, @PathVariable Long alunoId) {
        Turma turmaAtualizada = turmaService.removerAlunoDaTurma(turmaId, alunoId);
        return ResponseEntity.ok(new TurmaDTO(turmaAtualizada));
    }
}
