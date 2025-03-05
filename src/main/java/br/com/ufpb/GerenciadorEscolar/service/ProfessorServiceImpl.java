package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorRequest;
import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorResponse;
import br.com.ufpb.GerenciadorEscolar.mapper.ProfessorMapper;
import br.com.ufpb.GerenciadorEscolar.model.Professor;
import br.com.ufpb.GerenciadorEscolar.model.UserLogin;
import br.com.ufpb.GerenciadorEscolar.repository.ProfessorRepository;
import br.com.ufpb.GerenciadorEscolar.repository.UserLoginRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class ProfessorServiceImpl implements ProfessorServiceInterface {

    private final ProfessorRepository professorRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfessorMapper professorMapper;
    private final UserLoginRepository userLoginRepository;

    @Autowired
    public ProfessorServiceImpl(ProfessorRepository professorRepository,
                                PasswordEncoder passwordEncoder,
                                ProfessorMapper professorMapper,
                                UserLoginRepository userLoginRepository) {
        this.professorRepository = professorRepository;
        this.passwordEncoder = passwordEncoder;
        this.professorMapper = professorMapper;
        this.userLoginRepository = userLoginRepository;
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

        List<Map.Entry<String, String>> campos = Arrays.asList(
                new AbstractMap.SimpleEntry<>("Nome", professorRequest.nome()),
                new AbstractMap.SimpleEntry<>("Email", professorRequest.email()),
                new AbstractMap.SimpleEntry<>("CPF", professorRequest.cpf()),
                new AbstractMap.SimpleEntry<>("Departamento", professorRequest.departamento()),
                new AbstractMap.SimpleEntry<>("SIAPE", professorRequest.siape()),
                new AbstractMap.SimpleEntry<>("Senha", professorRequest.senha())
        );

        campos.forEach(campo -> {
            if (campo.getValue() == null) {
                throw new NullPointerException("❌ O campo " + campo.getKey() + " não pode ser nulo.");
            }

            if (campo.getValue().trim().isEmpty()) {
                throw new IllegalArgumentException("❌ O campo " + campo.getKey() + " não pode ser vazio.");
            }
        });

        Optional<Professor> professorExistenteEmail = professorRepository.findByEmailAndAtivoTrue(professorRequest.email());
        if (professorExistenteEmail.isPresent()) {
            log.warn("Tentativa de cadastrar professor com e-mail já existente: {}", professorRequest.email());
            throw new RuntimeException("Já existe um professor ativo cadastrado com esse e-mail.");
        }

        Optional<Professor> professorExistenteCpf = professorRepository.findByCpfAndAtivoTrue(professorRequest.cpf());
        if (professorExistenteCpf.isPresent()) {
            log.warn("Tentativa de cadastrar professor com CPF já existente: {}", professorRequest.cpf());
            throw new RuntimeException("Já existe um professor ativo cadastrado com esse CPF.");
        }

        Professor professor = professorMapper.toEntity(professorRequest);
        professor.setSenha(passwordEncoder.encode(professorRequest.senha()));
        professor.setAtivo(true);

        try {
            professorRepository.save(professor);
            log.info("Professor cadastrado com sucesso. ID: {}", professor.getId());

            UserLogin userLogin = new UserLogin(professorRequest.email(), professorRequest.senha(), professor);
            userLogin.setSenha(passwordEncoder.encode(professorRequest.senha())); // Codifica a senha
            userLoginRepository.save(userLogin);  // Salva o login do professor

            log.info("Professor e Login cadastrados com sucesso. ID: {}", professor.getId());
        } catch (Exception e) {
            log.error("Erro ao cadastrar professor: {}", e.getMessage());
            throw new RuntimeException("Erro ao cadastrar professor: " + e.getMessage(), e);
        }

        return professorMapper.toResponse(professor);
    }

    @Override
    public ProfessorResponse atualizarProfessor(Long id, ProfessorRequest professorRequest) {
        log.info("Iniciando atualização do professor com ID: {}", id);

        if (id == null) {
            log.error("O ID do professor não pode ser nulo.");
            throw new IllegalArgumentException("O ID do professor não pode ser nulo.");
        }

        Professor professor = professorRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> {
                    log.error("Professor não encontrado ou inativo. ID: {}", id);
                    return new RuntimeException("Professor não encontrado ou inativo.");
                });

        if (professorRequest.email() != null && !professorRequest.email().equals(professor.getEmail())) {
            log.info("Verificando se já existe outro professor ativo com o e-mail: {}", professorRequest.email());

            Optional<Professor> professorComMesmoEmail = professorRepository.findByEmailAndAtivoTrue(professorRequest.email());

            if (professorComMesmoEmail.isPresent() && !professorComMesmoEmail.get().getId().equals(id)) {
                log.warn("Tentativa de atualizar para um e-mail já existente: {}", professorRequest.email());
                throw new RuntimeException("Já existe outro professor ativo cadastrado com esse e-mail.");
            }

            log.info("Atualizando e-mail: {} → {}", professor.getEmail(), professorRequest.email());
            professor.setEmail(professorRequest.email());

            UserLogin userLogin = userLoginRepository.findByUsuarioAndAtivoTrue(professor)
                    .orElseThrow(() -> new RuntimeException("Login não encontrado para o Professor"));
            userLogin.setEmail(professorRequest.email());
            userLoginRepository.save(userLogin);
        }

        if (professorRequest.nome() != null) {
            log.info("Atualizando nome: {} → {}", professor.getNome(), professorRequest.nome());
            professor.setNome(professorRequest.nome());
        }
        if (professorRequest.cpf() != null) {
            log.info("Atualizando CPF: {} → {}", professor.getCpf(), professorRequest.cpf());
            professor.setCpf(professorRequest.cpf());
        }
        if (professorRequest.departamento() != null) {
            log.info("Atualizando departamento: {} → {}", professor.getDepartamento(), professorRequest.departamento());
            professor.setDepartamento(professorRequest.departamento());
        }
        if (professorRequest.siape() != null) {
            log.info("Atualizando SIAPE: {} → {}", professor.getSiape(), professorRequest.siape());
            professor.setSiape(professorRequest.siape());
        }

        if (professorRequest.senha() != null && !professorRequest.senha().trim().isEmpty()) {
            professor.setSenha(passwordEncoder.encode(professorRequest.senha()));
            log.info("Senha atualizada para o professor ID: {}", id);

            UserLogin userLogin = userLoginRepository.findByUsuarioAndAtivoTrue(professor)
                    .orElseThrow(() -> new RuntimeException("Login não encontrado para o Professor"));
            userLogin.setSenha(passwordEncoder.encode(professorRequest.senha()));
            userLoginRepository.save(userLogin);
        } else {
            log.info("Nenhuma senha nova fornecida. Mantendo a senha existente para o professor ID: {}", id);
        }

        professorRepository.save(professor);
        log.info("Professor atualizado com sucesso. ID: {}", professor.getId());

        return professorMapper.toResponse(professor);
    }


    @Override
    public void desativarProfessor(Long id) {
        log.info("Desativando professor com ID: {}", id);

        Professor professor = professorRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> {
                    log.error("❌ Professor não encontrado para desativação. ID: {}", id);
                    return new RuntimeException("Professor não encontrado");
                });

        UserLogin userLogin = userLoginRepository.findByUsuarioAndAtivoTrue(professor)
                .orElseThrow(() -> {
                    log.warn("Nenhum login encontrado para o professor ID: {}", id);
                    return new RuntimeException("Login não encontrado");
                });

        userLogin.setAtivo(false);
        userLoginRepository.save(userLogin);

        // Desativar o professor
        professor.setAtivo(false);
        professorRepository.save(professor);

        log.info("Professor e login desativados com sucesso. ID: {}", id);
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
