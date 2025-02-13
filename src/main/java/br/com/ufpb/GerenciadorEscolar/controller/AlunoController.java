package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.dto.AlunoDTO;
import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.service.AlunoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/alunos")
@CrossOrigin("*")
public class AlunoController {

    @Autowired
    private AlunoServiceImpl alunoService;

    /**
     * Listar todos os alunos ativos.
     */
    @GetMapping
    public ResponseEntity<List<AlunoDTO>> listarTodosAlunos() {
        List<AlunoDTO> alunosDTO = alunoService.listarAlunosAtivos()
                .stream()
                .map(aluno -> new AlunoDTO(
                        aluno.getId(),
                        aluno.getNome(),
                        aluno.getEmail(),
                        aluno.getCurso(),
                        aluno.getTurmas() != null ? aluno.getTurmas().stream()
                                .map(turma -> turma.getNome()) // Converte as turmas para apenas o nome delas
                                .toList() : List.of() // Retorna uma lista vazia caso seja null
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(alunosDTO);
    }

    /**
     * Buscar um aluno por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AlunoDTO> buscarAlunoPorId(@PathVariable Long id) {
        return alunoService.buscarAlunoPorId(id)
                .map(aluno -> ResponseEntity.ok(new AlunoDTO(
                        aluno.getId(),
                        aluno.getNome(),
                        aluno.getEmail(),
                        aluno.getCurso(),
                        aluno.getTurmas() != null ? aluno.getTurmas().stream()
                                .map(turma -> turma.getNome()) // Converte as turmas para apenas o nome delas
                                .toList() : List.of() // Retorna uma lista vazia caso seja null
                )))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Cadastrar um novo aluno.
     */
    @PostMapping
    public ResponseEntity<AlunoDTO> cadastrarAluno(@RequestBody Aluno aluno) {
        Aluno novoAluno = alunoService.cadastrarAluno(aluno);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AlunoDTO(
                        novoAluno.getId(),
                        novoAluno.getNome(),
                        novoAluno.getEmail(),
                        novoAluno.getCurso(),
                        novoAluno.getTurmas() != null ? novoAluno.getTurmas().stream()
                                .map(turma -> turma.getNome()) // Converte as turmas para apenas o nome delas
                                .toList() : List.of() // Retorna uma lista vazia caso seja null
                ));
    }

    /**
     * Atualizar os dados de um aluno existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AlunoDTO> atualizarAluno(@PathVariable Long id, @RequestBody Aluno aluno) {
        Aluno alunoAtualizado = alunoService.atualizarAluno(id, aluno);
        return ResponseEntity.ok(new AlunoDTO(
                alunoAtualizado.getId(),
                alunoAtualizado.getNome(),
                alunoAtualizado.getEmail(),
                alunoAtualizado.getCurso(),
                alunoAtualizado.getTurmas() != null ? alunoAtualizado.getTurmas().stream()
                        .map(turma -> turma.getNome()) // Converte as turmas para apenas o nome delas
                        .toList() : List.of() // Retorna uma lista vazia caso seja null
        ));
    }

    /**
     * Desativar um aluno.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> desativarAluno(@PathVariable Long id) {
        alunoService.desativarAluno(id);
        return ResponseEntity.ok("Aluno com ID " + id + " foi desativado com sucesso.");
    }
}
