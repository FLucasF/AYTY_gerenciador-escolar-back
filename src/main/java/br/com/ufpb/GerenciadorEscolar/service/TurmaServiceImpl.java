package br.com.ufpb.GerenciadorEscolar.service.impl;

import br.com.ufpb.GerenciadorEscolar.model.Turma;
import br.com.ufpb.GerenciadorEscolar.repository.TurmaRepository;
import br.com.ufpb.GerenciadorEscolar.service.TurmaServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TurmaServiceImpl implements TurmaServiceInterface {

    @Autowired
    private TurmaRepository turmaRepository;

    @Override
    public Turma criarTurma(Turma turma) {
        return turmaRepository.save(turma);
    }

    @Override
    public List<Turma> listarTodasTurmas() {
        return turmaRepository.findAll();
    }

    @Override
    public Optional<Turma> buscarTurmaPorId(Long id) {
        return turmaRepository.findById(id);
    }

    @Override
    public void deletarTurma(Long id) {
        turmaRepository.deleteById(id);
    }
}
