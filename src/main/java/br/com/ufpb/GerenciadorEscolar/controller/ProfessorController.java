package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.dto.ProfessorDTO;
import br.com.ufpb.GerenciadorEscolar.model.Professor;
import br.com.ufpb.GerenciadorEscolar.service.ProfessorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/professores")
@CrossOrigin("*")
public class ProfessorController {

    @Autowired
    private ProfessorServiceImpl professorService;

    /**
     * Listar todos os professores ativos.
     */
    @GetMapping
    public ResponseEntity<List<ProfessorDTO>> listarTodosProfessores() {
        List<ProfessorDTO> professoresDTO = professorService.listarProfessoresAtivos()
                .stream()
                .map(professor -> new ProfessorDTO(
                        professor.getId(),
                        professor.getNome(),
                        professor.getEmail(),
                        professor.getDepartamento(),
                        professor.getTurmas() != null ? professor.getTurmas().stream()
                                .map(turma -> turma.getNome()) // Converte a turma em apenas o nome dela
                                .toList() : List.of() // Se for null, retorna uma lista vazia
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(professoresDTO);
    }


    /**
     * Buscar um professor por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProfessorDTO> buscarProfessorPorId(@PathVariable Long id) {
        return professorService.buscarProfessorPorId(id)
                .map(professor -> ResponseEntity.ok(new ProfessorDTO(
                        professor.getId(),
                        professor.getNome(),
                        professor.getEmail(),
                        professor.getDepartamento(),
                        professor.getTurmas() != null ? professor.getTurmas().stream()
                                .map(turma -> turma.getNome()) // Converte a turma em apenas o nome dela
                                .toList() : List.of() // Se for null, retorna uma lista vazia
                )))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    /**
     * Cadastrar um novo professor.
     */
    @PostMapping
    public ResponseEntity<ProfessorDTO> cadastrarProfessor(@RequestBody Professor professor) {
        Professor novoProfessor = professorService.cadastrarProfessor(professor);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ProfessorDTO(
                        novoProfessor.getId(),
                        novoProfessor.getNome(),
                        novoProfessor.getEmail(),
                        novoProfessor.getDepartamento(),
                        novoProfessor.getTurmas() != null ? novoProfessor.getTurmas().stream()
                                .map(turma -> turma.getNome()) // Converte a turma em apenas o nome dela
                                .toList() : List.of() // Se for null, retorna uma lista vazia
                ));
    }


    /**
     * Atualizar os dados de um professor existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProfessorDTO> atualizarProfessor(@PathVariable Long id, @RequestBody Professor professor) {
        Professor professorAtualizado = professorService.atualizarProfessor(id, professor);
        return ResponseEntity.ok(new ProfessorDTO(
                professorAtualizado.getId(),
                professorAtualizado.getNome(),
                professorAtualizado.getEmail(),
                professorAtualizado.getDepartamento(),
                professorAtualizado.getTurmas() != null ? professorAtualizado.getTurmas().stream()
                        .map(turma -> turma.getNome()) // Converte a turma em apenas o nome dela
                        .toList() : List.of() // Se for null, retorna uma lista vazia
        ));
    }


    /**
     * Desativar um professor.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> desativarProfessor(@PathVariable Long id) {
        professorService.desativarProfessor(id);
        return ResponseEntity.ok("Professor com ID " + id + " foi desativado com sucesso.");
    }
}
