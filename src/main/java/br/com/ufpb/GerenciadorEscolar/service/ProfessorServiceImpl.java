package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.model.Professor;
import br.com.ufpb.GerenciadorEscolar.repository.ProfessorRepository;
import br.com.ufpb.GerenciadorEscolar.service.interfaces.ProfessorServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfessorServiceImpl implements ProfessorServiceInterface {

    @Autowired
    private ProfessorRepository professorRepository;

    @Override
    public List<Professor> listarProfessoresAtivos() {
        return professorRepository.findAllByAtivoTrue();
    }

    @Override
    public Optional<Professor> buscarProfessorPorId(Long id) {
        return professorRepository.findByIdAndAtivoTrue(id);
    }

    @Override
    public Professor cadastrarProfessor(Professor professor) {
        professor.setAtivo(true); // Define como ativo ao cadastrar
        return professorRepository.save(professor);
    }

    @Override
    public Professor atualizarProfessor(Long id, Professor professorAtualizado) {
        return professorRepository.findById(id)
                .map(professor -> {
                    professor.setNome(professorAtualizado.getNome());
                    professor.setEmail(professorAtualizado.getEmail());
                    professor.setDepartamento(professorAtualizado.getDepartamento());
                    return professorRepository.save(professor);
                })
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));
    }

    @Override
    public void desativarProfessor(Long id) {
        professorRepository.findById(id)
                .ifPresent(professor -> {
                    professor.setAtivo(false);
                    professorRepository.save(professor);
                });
    }
}
