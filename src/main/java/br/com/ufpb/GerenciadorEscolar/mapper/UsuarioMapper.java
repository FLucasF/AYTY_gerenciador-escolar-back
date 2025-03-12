package br.com.ufpb.GerenciadorEscolar.mapper;

import br.com.ufpb.GerenciadorEscolar.model.dto.usuario.UsuarioResponse;
import br.com.ufpb.GerenciadorEscolar.model.entity.Administrador;
import br.com.ufpb.GerenciadorEscolar.model.entity.Aluno;
import br.com.ufpb.GerenciadorEscolar.model.entity.Professor;
import br.com.ufpb.GerenciadorEscolar.model.entity.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    default UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getCpf(),
                getRole(usuario),

                usuario instanceof Aluno ? ((Aluno) usuario).getCurso() : null,
                usuario instanceof Administrador ? ((Administrador) usuario).getSetor() : null,
                usuario instanceof Professor ? ((Professor) usuario).getDepartamento() : null
        );
    }

    private String getRole(Usuario usuario) {
        if (usuario instanceof Administrador) return "ADMINISTRADOR";
        if (usuario instanceof Professor) return "PROFESSOR";
        if (usuario instanceof Aluno) return "ALUNO";
        return "DESCONHECIDO";
    }
}
