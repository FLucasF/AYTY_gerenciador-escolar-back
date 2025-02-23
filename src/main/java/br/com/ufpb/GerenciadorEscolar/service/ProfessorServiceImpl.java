package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorRequest;
import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorResponse;
import br.com.ufpb.GerenciadorEscolar.mapper.ProfessorMapper;
import br.com.ufpb.GerenciadorEscolar.model.Professor;
import br.com.ufpb.GerenciadorEscolar.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfessorServiceImpl implements ProfessorServiceInterface {

    private final ProfessorRepository professorRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfessorMapper professorMapper;

    @Autowired
    public ProfessorServiceImpl(ProfessorRepository professorRepository,
                                PasswordEncoder passwordEncoder,
                                ProfessorMapper professorMapper) {
        this.professorRepository = professorRepository;
        this.passwordEncoder = passwordEncoder;
        this.professorMapper = professorMapper;
    }

    @Override
    public Page<ProfessorResponse> listarProfessoresAtivos(Pageable pageable) {
        return professorRepository.findAllByAtivoTrue(pageable)
                .map(professorMapper::toResponse);
    }

    @Override
    public Optional<ProfessorResponse> buscarProfessorPorId(Long id) {
        return professorRepository.findByIdAndAtivoTrue(id)
                .map(professorMapper::toResponse);
    }

    @Override
    public ProfessorResponse cadastrarProfessor(ProfessorRequest professorRequest) {
        Professor professor = professorMapper.toEntity(professorRequest);
        professor.setSenha(passwordEncoder.encode(professorRequest.senha()));
        professor.setAtivo(true);
        professorRepository.save(professor);
        System.out.println("Role do professor cadastrado: " + professor.getRole());
        return professorMapper.toResponse(professor);
    }

    @Override
    public ProfessorResponse atualizarProfessor(Long id, ProfessorRequest professorRequest) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        professor.setNome(professorRequest.nome());
        professor.setEmail(professorRequest.email());
        professor.setDepartamento(professorRequest.departamento());

        professorRepository.save(professor);
        return professorMapper.toResponse(professor);
    }

    @Override
    public void desativarProfessor(Long id) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));
        professor.setAtivo(false);
        professorRepository.save(professor);
    }

    @Override
    public Optional<Professor> findByEmail(String email) {
        return professorRepository.findByEmailAndAtivoTrue(email);
    }
}
