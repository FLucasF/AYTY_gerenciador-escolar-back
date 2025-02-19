package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.dto.AlunoDTO;
import br.com.ufpb.GerenciadorEscolar.dto.TurmaDTO;
import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.model.Professor;
import br.com.ufpb.GerenciadorEscolar.model.Turma;
import br.com.ufpb.GerenciadorEscolar.repository.ProfessorRepository;
import br.com.ufpb.GerenciadorEscolar.repository.TurmaRepository;
import br.com.ufpb.GerenciadorEscolar.service.interfaces.TurmaServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Map;

@RestController
@RequestMapping("/turmas")
public class TurmaController {

    @Autowired
    private TurmaServiceInterface turmaService;

    @Autowired
    TurmaRepository turmaRepository;

    @Autowired
    private ProfessorRepository professorRepository;

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

    @PutMapping("/{id}")
    public ResponseEntity<TurmaDTO> atualizarTurma(@PathVariable Long id, @RequestBody Turma turmaAtualizada) {
        Turma turma = turmaService.atualizarTurma(id, turmaAtualizada);
        return ResponseEntity.ok(new TurmaDTO(turma));
    }


    @GetMapping
    public ResponseEntity<List<TurmaDTO>> listarTodasTurmas() {
        List<TurmaDTO> turmasDTO = turmaService.listarTodasTurmas().stream()
                .map(TurmaDTO::new) // Convertendo cada `Turma` para `TurmaDTO`
                .collect(Collectors.toList());
        return ResponseEntity.ok(turmasDTO);
    }

    @GetMapping("/{turmaId}/alunos")
    public ResponseEntity<List<AlunoDTO>> listarAlunosPorTurma(@PathVariable Long turmaId) {
        List<Aluno> alunos = turmaService.listarAlunosPorTurma(turmaId);
        List<AlunoDTO> alunosDTO = alunos.stream()
                .map(aluno -> new AlunoDTO(
                        aluno.getId(),
                        aluno.getNome(),
                        aluno.getEmail(),
                        aluno.getCurso(),
                        aluno.getTurmas() != null ? aluno.getTurmas().stream().map(turma -> turma.getNome()).collect(Collectors.toList()) : List.of()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(alunosDTO);
    }


    @GetMapping("/{id}")
    public ResponseEntity<TurmaDTO> buscarTurmaPorId(@PathVariable Long id) {
        Optional<Turma> turma = turmaService.buscarTurmaPorId(id);
        return turma.map(value -> ResponseEntity.ok(new TurmaDTO(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletarTurma(@PathVariable Long id) {
        turmaService.deletarTurma(id);

        Map<String, String> resposta = new HashMap<>();
        resposta.put("mensagem", "Turma deletada com sucesso.");

        return ResponseEntity.ok(resposta);
    }

}
