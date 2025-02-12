package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.model.Professor;
import br.com.ufpb.GerenciadorEscolar.model.Administrador;
import br.com.ufpb.GerenciadorEscolar.repository.AlunoRepository;
import br.com.ufpb.GerenciadorEscolar.repository.ProfessorRepository;
import br.com.ufpb.GerenciadorEscolar.repository.AdministradorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.com.ufpb.GerenciadorEscolar.model.*;
import br.com.ufpb.GerenciadorEscolar.service.factory.UsuarioFactory;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    public Usuario criarNovoUsuario(Class<? extends Usuario> tipo, String nome, String email, String senha, String extraInfo) {
        Usuario usuario = UsuarioFactory.criarUsuario(tipo, nome, email, senha, extraInfo);

        if (usuario instanceof Aluno) {
            return alunoRepository.save((Aluno) usuario);
        } else if (usuario instanceof Professor) {
            return professorRepository.save((Professor) usuario);
        } else if (usuario instanceof Administrador) {
            return administradorRepository.save((Administrador) usuario);
        } else {
            throw new IllegalArgumentException("Tipo de usuário inválido.");
        }
    }

    public Optional<?> buscarPorEmail(String email) {
        Optional<Aluno> aluno = alunoRepository.findByEmail(email);
        if (aluno.isPresent()) {
            return aluno;
        }

        Optional<Professor> professor = professorRepository.findByEmail(email);
        if (professor.isPresent()) {
            return professor;
        }

        Optional<Administrador> admin = administradorRepository.findByEmail(email);
        return admin;
    }
}
