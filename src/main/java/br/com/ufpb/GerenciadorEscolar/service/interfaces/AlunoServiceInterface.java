package br.com.ufpb.GerenciadorEscolar.service.interfaces;

import br.com.ufpb.GerenciadorEscolar.dto.AlunoDTO;
import br.com.ufpb.GerenciadorEscolar.model.Aluno;

import java.util.List;
import java.util.Optional;

public interface AlunoServiceInterface {
    Optional<Aluno> buscarAlunoPorId(Long id);
    List<Aluno> listarAlunosAtivos();
    Aluno cadastrarAluno(Aluno aluno);
    Aluno atualizarAluno(Long id, Aluno novosDados);
    void desativarAluno(Long id);
}
