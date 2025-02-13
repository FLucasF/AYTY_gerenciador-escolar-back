package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.dto.TurmaDTO;
import br.com.ufpb.GerenciadorEscolar.model.Turma;
import br.com.ufpb.GerenciadorEscolar.service.interfaces.TurmaServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/turmas")
public class TurmaController {

    @Autowired
    private TurmaServiceInterface turmaService;

    @PostMapping
    public ResponseEntity<TurmaDTO> criarTurma(@RequestBody Turma turma) {
        Turma novaTurma = turmaService.criarTurma(turma);
        return ResponseEntity.ok(new TurmaDTO(novaTurma));
    }

    @PostMapping("/{turmaId}/matricular/{alunoId}")
    public ResponseEntity<TurmaDTO> matricularAluno(@PathVariable Long turmaId, @PathVariable Long alunoId) {
        Turma turma = turmaService.matricularAluno(turmaId, alunoId);
        return ResponseEntity.ok(new TurmaDTO(turma));
    }

    @GetMapping
    public ResponseEntity<List<TurmaDTO>> listarTodasTurmas() {
        List<TurmaDTO> turmasDTO = turmaService.listarTodasTurmas().stream()
                .map(TurmaDTO::new) // Convertendo cada `Turma` para `TurmaDTO`
                .collect(Collectors.toList());
        return ResponseEntity.ok(turmasDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TurmaDTO> buscarTurmaPorId(@PathVariable Long id) {
        Optional<Turma> turma = turmaService.buscarTurmaPorId(id);
        return turma.map(value -> ResponseEntity.ok(new TurmaDTO(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
