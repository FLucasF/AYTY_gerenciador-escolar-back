package br.com.ufpb.GerenciadorEscolar.service.factory;

import br.com.ufpb.GerenciadorEscolar.model.Administrador;
import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.model.Professor;
import br.com.ufpb.GerenciadorEscolar.model.Usuario;

public class UsuarioFactory {

    public static Usuario criarUsuario(Class<? extends Usuario> tipo, String nome, String email, String senha, String extraInfo) {
        if (tipo.equals(Aluno.class)) {
            Aluno aluno = new Aluno();
            aluno.setNome(nome);
            aluno.setEmail(email);
            aluno.setSenha(senha);
            aluno.setCurso(extraInfo);
            return aluno;
        }
        else if (tipo.equals(Professor.class)) {
            Professor professor = new Professor();
            professor.setNome(nome);
            professor.setEmail(email);
            professor.setSenha(senha);
            professor.setDepartamento(extraInfo);
            return professor;
        }
        else if (tipo.equals(Administrador.class)) {
            Administrador admin = new Administrador();
            admin.setNome(nome);
            admin.setEmail(email);
            admin.setSenha(senha);
            admin.setSetor(extraInfo);
            return admin;
        }
        throw new IllegalArgumentException("Tipo de usuário inválido.");
    }
}
