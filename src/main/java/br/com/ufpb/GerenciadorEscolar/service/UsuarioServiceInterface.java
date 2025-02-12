package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.model.Usuario;
import java.util.Optional;

public interface UsuarioServiceInterface {
    Usuario criarNovoUsuario(Class<? extends Usuario> tipo, String nome, String email, String senha, String extraInfo);
    Optional<Usuario> buscarPorId(Long id);
    Optional<Usuario> buscarPorEmail(String email);
    Usuario atualizarUsuario(Long id, Usuario novosDados);
    void deletarUsuario(Long id);
}
