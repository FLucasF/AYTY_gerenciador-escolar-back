package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.repository.AlunoRepository;
import br.com.ufpb.GerenciadorEscolar.service.interfaces.AlunoServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlunoServiceImpl implements AlunoServiceInterface {

    @Autowired
    private AlunoRepository alunoRepository;

    /**
     * Listar todos os alunos ativos.
     */
    @Override
    public List<Aluno> listarAlunosAtivos() {
        return alunoRepository.findAllByAtivoTrue();
    }

    /**
     * Buscar um aluno por ID.
     */
    @Override
    public Optional<Aluno> buscarAlunoPorId(Long id) {
        return alunoRepository.findByIdAndAtivoTrue(id);
    }

    /**
     * Cadastrar um novo aluno.
     */
    @Override
    public Aluno cadastrarAluno(Aluno aluno) {
        aluno.setAtivo(true); // Garante que o aluno começa como ativo
        return alunoRepository.save(aluno);
    }

    /**
     * Atualizar um aluno existente.
     */
    @Override
    public Aluno atualizarAluno(Long id, Aluno novosDados) {
        Aluno alunoExistente = alunoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        alunoExistente.setNome(novosDados.getNome());
        alunoExistente.setEmail(novosDados.getEmail());
        alunoExistente.setCurso(novosDados.getCurso());

        return alunoRepository.save(alunoExistente);
    }

    /**
     * Desativar um aluno.
     */
    @Override
    public void desativarAluno(Long id) {
        Aluno aluno = alunoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        aluno.setAtivo(false);
        alunoRepository.save(aluno);
    }

    @Override
    public Optional<Aluno> findByEmail(String email) {
        return alunoRepository.findByEmail(email);
    }

}
