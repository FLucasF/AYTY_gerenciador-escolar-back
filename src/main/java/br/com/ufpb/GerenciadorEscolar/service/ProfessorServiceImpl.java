package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorRequest;
import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorResponse;
import br.com.ufpb.GerenciadorEscolar.mapper.ProfessorMapper;
import br.com.ufpb.GerenciadorEscolar.model.Administrador;
import br.com.ufpb.GerenciadorEscolar.model.Aluno;
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
        if (id == null) throw new NullPointerException("ID não pode ser nulo");

        if (id <= 0) throw new IllegalArgumentException("ID não pode ser nulo ou inválido");

        log.info("Buscando professor por ID: {}", id);
        Optional<ProfessorResponse> response = professorRepository.findByIdAndAtivoTrue(id)
                .map(professorMapper::toResponse);

        if (response.isEmpty()) {
            log.warn("Professor não encontrado para o ID: {}", id);
        }
        return response;
    }

    @Override
    public ProfessorResponse cadastrarProfessor(ProfessorRequest professorRequest) {
        log.info("Verificando se já existe professor ativo com o e-mail {} ou CPF {}", professorRequest.email(), professorRequest.cpf());

        Optional<Professor> professorComMesmoEmail = professorRepository.findByEmailAndAtivoTrue(professorRequest.email());
        Optional<Professor> professorComMesmoCpf = professorRepository.findByCpfAndAtivoTrue(professorRequest.cpf());

        if (professorComMesmoEmail.isPresent()) {
            log.warn("Tentativa de cadastrar professor com e-mail duplicado: {}", professorRequest.email());
            throw new RuntimeException("Já existe um professor ativo cadastrado com esse e-mail.");
        }

        if (professorComMesmoCpf.isPresent()) {
            log.warn("Tentativa de cadastrar professor com CPF duplicado: {}", professorRequest.cpf());
            throw new RuntimeException("Já existe um professor ativo cadastrado com esse CPF.");
        }

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
                throw new NullPointerException(campo.getKey() + " não pode ser nulo.");
            }

            if (campo.getValue().trim().isEmpty()) {
                throw new IllegalArgumentException(campo.getKey() + " não pode ser vazio.");
            }
        });

        Professor professor = professorMapper.toEntity(professorRequest);
        professor.setSenha(passwordEncoder.encode(professorRequest.senha()));
        professor.setAtivo(true);

        try {
            professorRepository.save(professor);
            log.info("Professor cadastrado com sucesso. ID: {}", professor.getId());

            UserLogin userLogin = new UserLogin(professorRequest.email(), professorRequest.senha(), professor);
            userLogin.setSenha(passwordEncoder.encode(professorRequest.senha()));
            userLoginRepository.save(userLogin);

            log.info("Professor e Login cadastrados com sucesso. ID: {}", professor.getId());
        } catch (Exception e) {
            log.error("Erro ao cadastrar professor: {}", e.getMessage());
            throw new RuntimeException("Erro ao cadastrar professor: " + e.getMessage(), e);
        }

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

        userLoginRepository.findByUsuarioAndAtivoTrue(professor).ifPresent(userLogin -> {
            if (userLogin.isAtivo()) {
                userLogin.setAtivo(false);
                userLoginRepository.save(userLogin);
                log.info("Login do professor desativado com sucesso. ID: {}", id);
            } else {
                log.info("Login já estava inativo. Nenhuma alteração necessária.");
            }
        });

        professor.setAtivo(false);
        professorRepository.save(professor);
        log.info("Professor desativado com sucesso. ID: {}", id);
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
                    log.error("❌ Professor não encontrado ou inativo. ID: {}", id);
                    return new RuntimeException("Professor não encontrado ou inativo.");
                });

        UserLogin userLogin = userLoginRepository.findByUsuarioAndAtivoTrue(professor)
                .orElseThrow(() -> new RuntimeException("Login não encontrado para o Professor"));

        boolean dadosAlterados = false;

        if (!professorRequest.nome().equals(professor.getNome())) {
            professor.setNome(professorRequest.nome());
            dadosAlterados = true;
        }

        if (!professorRequest.cpf().equals(professor.getCpf())) {
            professor.setCpf(professorRequest.cpf());
            dadosAlterados = true;
        }

        if (!professorRequest.departamento().equals(professor.getDepartamento())) {
            professor.setDepartamento(professorRequest.departamento());
            dadosAlterados = true;
        }

        if (!professorRequest.siape().equals(professor.getSiape())) {
            professor.setSiape(professorRequest.siape());
            dadosAlterados = true;
        }

        if (!professorRequest.email().equals(professor.getEmail())) {
            professor.setEmail(professorRequest.email());
            userLogin.setEmail(professorRequest.email());
            dadosAlterados = true;
        }

        if (professorRequest.senha() != null && !professorRequest.senha().trim().isEmpty()) {
            String senhaCriptografada = passwordEncoder.encode(professorRequest.senha());
            if (!passwordEncoder.matches(professorRequest.senha(), professor.getSenha())) {
                log.info("Atualizando senha do professor ID: {}", id);
                professor.setSenha(senhaCriptografada);
                userLogin.setSenha(senhaCriptografada);
                dadosAlterados = true;
            } else {
                log.info("Senha informada é igual à senha atual. Nenhuma alteração realizada.");
                throw new NenhumaAlteracaoRealizadaException();
            }
        }


        if (!dadosAlterados) {
            throw new NenhumaAlteracaoRealizadaException();
        }

        professorRepository.save(professor);
        userLoginRepository.save(userLogin);

        log.info("Professor atualizado com sucesso. ID: {}", professor.getId());
        return professorMapper.toResponse(professor);
    }

    @Override
    public Optional<Professor> findByEmail(String email) {
        if (email == null) throw new NullPointerException("Email não pode ser nulo");

        if(email.trim().isEmpty()) throw new IllegalArgumentException("Email não pode ser vazio");

        log.debug("Buscando aluno por email: {}", email);
        Optional<Professor> professorOpt = professorRepository.findByEmailAndAtivoTrue(email);
        if (professorOpt.isEmpty()) {
            log.warn("Aluno não encontrado para o email: {}", email);
        }
        return professorOpt;
    }


}
