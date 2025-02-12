package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.service.UsuarioService;
import br.com.ufpb.GerenciadorEscolar.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/criar")
    public Usuario criarUsuario(@RequestParam String tipo, @RequestParam String nome, @RequestParam String email, @RequestParam String senha, @RequestParam String extraInfo) {
        Class<? extends Usuario> classeUsuario;
        switch (tipo.toUpperCase()) {
            case "ALUNO":
                classeUsuario = br.com.ufpb.GerenciadorEscolar.model.Aluno.class;
                break;
            case "PROFESSOR":
                classeUsuario = br.com.ufpb.GerenciadorEscolar.model.Professor.class;
                break;
            case "ADMINISTRADOR":
                classeUsuario = br.com.ufpb.GerenciadorEscolar.model.Administrador.class;
                break;
            default:
                throw new IllegalArgumentException("Tipo de usuário inválido.");
        }
        return usuarioService.criarNovoUsuario(classeUsuario, nome, email, senha, extraInfo);
    }

    @GetMapping("/email/{email}")
    public Object buscarPorEmail(@PathVariable String email) {
        return usuarioService.buscarPorEmail(email).orElse(null);
    }

}
