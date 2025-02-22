package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorRequest;
import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorResponse;
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

    @Autowired
    public ProfessorServiceImpl(ProfessorRepository professorRepository, PasswordEncoder passwordEncoder) {
        this.professorRepository = professorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Page<ProfessorResponse> listarProfessoresAtivos(Pageable pageable) {
        return professorRepository.findAllByAtivoTrue(pageable)
                .map(prof -> new ProfessorResponse(prof.getId(), prof.getNome(), prof.getEmail(), prof.getCpf(), prof.getDepartamento(), prof.getSiape()));
    }

    @Override
    public Optional<ProfessorResponse> buscarProfessorPorId(Long id) {
        return professorRepository.findByIdAndAtivoTrue(id)
                .map(professor -> new ProfessorResponse(professor.getId(), professor.getNome(), professor.getEmail(), professor.getCpf(), professor.getDepartamento(), professor.getSiape()));
    }


    @Override
    public ProfessorResponse cadastrarProfessor(ProfessorRequest professorRequest) {
        Professor professor = new Professor(
                professorRequest.nome(),
                professorRequest.email(),
                passwordEncoder.encode(professorRequest.senha()),
                professorRequest.cpf(),
                professorRequest.departamento(),
                professorRequest.siape()
        );
        professor.setAtivo(true);
        professorRepository.save(professor);

        return new ProfessorResponse(professor.getId(), professor.getNome(), professor.getEmail(), professor.getCpf(), professor.getDepartamento(), professor.getSiape());
    }

    @Override
    public ProfessorResponse atualizarProfessor(Long id, ProfessorRequest professorRequest) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        professor.setNome(professorRequest.nome());
        professor.setEmail(professorRequest.email());
        professor.setDepartamento(professorRequest.departamento());

        professorRepository.save(professor);
        return new ProfessorResponse(professor.getId(), professor.getNome(), professor.getEmail(), professor.getCpf(), professor.getDepartamento(), professor.getSiape());
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
