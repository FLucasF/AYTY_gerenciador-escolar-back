package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorRequest;
import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorResponse;
import br.com.ufpb.GerenciadorEscolar.mapper.ProfessorMapper;
import br.com.ufpb.GerenciadorEscolar.model.Professor;
import br.com.ufpb.GerenciadorEscolar.model.UserLogin;
import br.com.ufpb.GerenciadorEscolar.repository.ProfessorRepository;
import br.com.ufpb.GerenciadorEscolar.repository.UserLoginRepository;
import br.com.ufpb.GerenciadorEscolar.util.UsuarioUtils;
import lombok.extern.slf4j.Slf4j;
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
    private final UserLoginRepository userLoginRepository;

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
    public ProfessorResponse cadastrarProfessor(ProfessorRequest professorRequest) {
        log.info("Verificando se já existe professor ativo com o e-mail {} ou CPF {}", professorRequest.email(), professorRequest.cpf());

        if (professorRepository.findByEmailAndAtivoTrue(professorRequest.email()).isPresent()) {
            throw new EmailJaCadastradoException("Já existe um professor ativo cadastrado com esse e-mail.");
        }

        if (professorRepository.findByCpfAndAtivoTrue(professorRequest.cpf()).isPresent()) {
            throw new CpfJaCadastradoException("Já existe um professor ativo cadastrado com esse CPF.");
        }

        if (professorRepository.findBySiapeAndAtivoTrue(professorRequest.siape()).isPresent()) {
            throw new SiapeJaCadastradoException("Já existe um administrador ativo cadastrado com esse SIAPE.");
        }

        Professor professor = professorMapper.toEntity(professorRequest);
        professor.setSenha(passwordEncoder.encode(professorRequest.senha()));

        professorRepository.save(professor);
        log.info("Professor cadastrado com sucesso. ID: {}", professor.getId());

        UserLogin userLogin = new UserLogin();
        userLogin.setEmail(professor.getEmail());
        userLogin.setUsuario(professor);
        userLogin.setSenha(passwordEncoder.encode(professorRequest.senha()));
        userLoginRepository.save(userLogin);

        log.info("Professor e Login cadastrados com sucesso. ID: {}", professor.getId());
        return professorMapper.toResponse(professor);
    }

    @Override
    public Page<ProfessorResponse> listarProfessoresAtivos(Pageable pageable) {
        log.info("Listando professores ativos com paginação: {}", pageable);
        return professorRepository.findAllByAtivoTrue(pageable)
                .map(professorMapper::toResponse);
    }

    @Override
    public ProfessorResponse buscarProfessorPorId(Long id) {
        log.info("Buscando professor por ID: {}", id);

        Professor professor = professorRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> {
                    log.warn("Professor não encontrado para o ID: {}", id);
                    return new ProfessorNaoEncontradoException("Professor não encontrado.");
                });

        return professorMapper.toResponse(professor);
    }

    @Override
    public ProfessorResponse atualizarProfessor(Long id, ProfessorRequest professorRequest) {
        log.info("Atualizando professor com ID: {}", id);

        Professor professor = professorRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ProfessorNaoEncontradoException("Professor não encontrado ou inativo."));

        UserLogin userLogin = userLoginRepository.findByUsuarioAndAtivoTrue(professor)
                .orElseThrow(() -> new ProfessorNaoEncontradoException("Login não encontrado para o Professor"));

        boolean dadosAlterados = UsuarioUtils.atualizarDadosUsuario(
                professor,
                userLogin,
                professorRequest.nome(),
                professorRequest.email(),
                professorRequest.cpf(),
                professorRequest.senha(),
                passwordEncoder
        );

        if (professorRequest.departamento() != null && !professorRequest.departamento().equals(professor.getDepartamento())) {
            professor.setDepartamento(professorRequest.departamento());
            dadosAlterados = true;
        }

        if (professorRequest.siape() != null && !professorRequest.siape().equals(professor.getSiape())) {
            if (professorRepository.findBySiapeAndAtivoTrue(professorRequest.siape()).isPresent()) {
                throw new SiapeJaCadastradoException("Já existe um professor ativo cadastrado com esse SIAPE.");
            }
            professor.setSiape(professorRequest.siape());
            dadosAlterados = true;
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
    public void desativarProfessor(Long id) {
        log.info("Desativando professor com ID: {}", id);

        Professor professor = professorRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ProfessorNaoEncontradoException("Professor não encontrado"));

        userLoginRepository.findByUsuarioAndAtivoTrue(professor).ifPresent(userLogin -> {
            userLogin.setAtivo(false);
            userLoginRepository.save(userLogin);
            log.info("Login do professor desativado. ID: {}", id);
        });

        professor.setAtivo(false);
        professorRepository.save(professor);
        log.info("Professor desativado. ID: {}", id);
    }
}
