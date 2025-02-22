package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoResponse;
import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorResponse;
import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorResponse;
import br.com.ufpb.GerenciadorEscolar.dto.usuario.UsuarioResponse;
import br.com.ufpb.GerenciadorEscolar.service.AlunoServiceImpl;
import br.com.ufpb.GerenciadorEscolar.service.ProfessorServiceImpl;
import br.com.ufpb.GerenciadorEscolar.service.AdministradorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final AlunoServiceImpl alunoService;
    private final ProfessorServiceImpl professorService;
    private final AdministradorServiceImpl administradorService;

    public UsuarioController(AlunoServiceImpl alunoService,
                             ProfessorServiceImpl professorService,
                             AdministradorServiceImpl administradorService) {
        this.alunoService = alunoService;
        this.professorService = professorService;
        this.administradorService = administradorService;
    }

    /**
     * 🔹 Lista todos os usuários (Alunos, Professores e Administradores).
     */
    @GetMapping
    public ResponseEntity<Page<UsuarioResponse>> listarUsuarios(Pageable pageable) {
        List<UsuarioResponse> usuarios = Stream.concat(
                Stream.concat(
                        alunoService.listarAlunosAtivos(pageable).getContent().stream()
                                .map(aluno -> new UsuarioResponse(aluno.id(), aluno.nome(), aluno.email(), aluno.cpf(), "ALUNO")),
                        professorService.listarProfessoresAtivos(pageable).getContent().stream()
                                .map(professor -> new UsuarioResponse(professor.id(), professor.nome(), professor.email(), professor.cpf(), "PROFESSOR"))
                ),
                administradorService.listarAdministradoresAtivos(pageable).getContent().stream()
                        .map(administrador -> new UsuarioResponse(administrador.id(), administrador.nome(), administrador.email(), administrador.cpf(), "ADMINISTRADOR"))
        ).collect(Collectors.toList());

        Page<UsuarioResponse> usuariosPage = new PageImpl<>(usuarios, pageable, usuarios.size());
        return ResponseEntity.ok(usuariosPage);
    }

}
