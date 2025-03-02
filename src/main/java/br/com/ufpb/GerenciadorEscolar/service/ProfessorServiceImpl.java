package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorRequest;
import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorResponse;
import br.com.ufpb.GerenciadorEscolar.mapper.ProfessorMapper;
import br.com.ufpb.GerenciadorEscolar.model.Professor;
import br.com.ufpb.GerenciadorEscolar.repository.ProfessorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
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
        log.info("Listando professores ativos com paginação: {}", pageable);
        Page<ProfessorResponse> page = professorRepository.findAllByAtivoTrue(pageable)
                .map(professorMapper::toResponse);
        log.info("Total de professores ativos encontrados: {}", page.getTotalElements());
        return page;
    }

    @Override
    public Optional<ProfessorResponse> buscarProfessorPorId(Long id) {
        log.info("Buscando professor por ID: {}", id);
        Optional<ProfessorResponse> professorResponse = professorRepository.findByIdAndAtivoTrue(id)
                .map(professorMapper::toResponse);
        if (professorResponse.isEmpty()) {
            log.warn("Professor não encontrado para o ID: {}", id);
        }
        return professorResponse;
    }

    @Override
    public ProfessorResponse cadastrarProfessor(ProfessorRequest professorRequest) {
        log.info("Iniciando cadastro de professor: {}", professorRequest);
        Professor professor = professorMapper.toEntity(professorRequest);
        professor.setSenha(passwordEncoder.encode(professorRequest.senha()));
        professor.setAtivo(true);
        professorRepository.save(professor);
        log.info("Professor cadastrado com sucesso. Role: {}", professor.getRole());
        return professorMapper.toResponse(professor);
    }

    @Override
    public ProfessorResponse atualizarProfessor(Long id, ProfessorRequest professorRequest) {
        log.info("Atualizando professor com ID: {}", id);
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Professor não encontrado para o ID: {}", id);
                    return new RuntimeException("Professor não encontrado");
                });

        professor.setNome(professorRequest.nome());
        professor.setEmail(professorRequest.email());
        professor.setDepartamento(professorRequest.departamento());
        professorRepository.save(professor);
        log.info("Professor atualizado com sucesso. ID: {}", professor.getId());
        return professorMapper.toResponse(professor);
    }

    @Override
    public void desativarProfessor(Long id) {
        log.info("Desativando professor com ID: {}", id);
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Professor não encontrado para desativação com ID: {}", id);
                    return new RuntimeException("Professor não encontrado");
                });
        professor.setAtivo(false);
        professorRepository.save(professor);
        log.info("Professor desativado com sucesso. ID: {}", id);
    }

    @Override
    public Optional<Professor> findByEmail(String email) {
        log.debug("Buscando professor por email: {}", email);
        Optional<Professor> professorOpt = professorRepository.findByEmailAndAtivoTrue(email);
        if (professorOpt.isEmpty()) {
            log.warn("Professor não encontrado para o email: {}", email);
        }
        return professorOpt;
    }
}
