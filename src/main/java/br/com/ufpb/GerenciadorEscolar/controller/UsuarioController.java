package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.dto.usuario.UsuarioResponse;
import br.com.ufpb.GerenciadorEscolar.service.UsuarioServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioServiceImpl usuarioService;

    public UsuarioController(UsuarioServiceImpl usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * 🔹 Lista todos os usuários (Alunos, Professores e Administradores) de forma paginada corretamente.
     */
    @GetMapping
    public ResponseEntity<Page<UsuarioResponse>> listarUsuarios(Pageable pageable) {
        return ResponseEntity.ok(usuarioService.listarUsuarios(pageable));
    }
}
