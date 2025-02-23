package br.com.ufpb.GerenciadorEscolar.mapper;

import br.com.ufpb.GerenciadorEscolar.dto.usuario.UsuarioResponse;
import br.com.ufpb.GerenciadorEscolar.model.Administrador;
import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.model.Professor;
import br.com.ufpb.GerenciadorEscolar.model.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    default UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getCpf(),
                getRole(usuario)
        );
    }

    private String getRole(Usuario usuario) {
        if (usuario instanceof Administrador) return "ADMINISTRADOR";
        if (usuario instanceof Professor) return "PROFESSOR";
        if (usuario instanceof Aluno) return "ALUNO";
        return "DESCONHECIDO";
    }
}
