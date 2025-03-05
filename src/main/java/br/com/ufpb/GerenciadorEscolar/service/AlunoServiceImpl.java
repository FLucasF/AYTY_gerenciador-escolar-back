package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoRequest;
import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoResponse;
import br.com.ufpb.GerenciadorEscolar.mapper.AlunoMapper;
import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.model.UserLogin;
import br.com.ufpb.GerenciadorEscolar.repository.AlunoRepository;
import br.com.ufpb.GerenciadorEscolar.repository.UserLoginRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.*;

@Service
@Slf4j
public class AlunoServiceImpl implements AlunoServiceInterface {

    private final AlunoRepository alunoRepository;
    private final PasswordEncoder passwordEncoder;
    private final AlunoMapper alunoMapper;
    private final UserLoginRepository userLoginRepository;

    @Autowired
    public AlunoServiceImpl(AlunoRepository alunoRepository,
                            PasswordEncoder passwordEncoder,
                            AlunoMapper alunoMapper,
                            UserLoginRepository userLoginRepository) {
        this.alunoRepository = alunoRepository;
        this.passwordEncoder = passwordEncoder;
        this.alunoMapper = alunoMapper;
        this.userLoginRepository = userLoginRepository;
    }

    @Override
    public Page<AlunoResponse> listarAlunosAtivos(Pageable pageable) {
        log.info("Listando alunos ativos com paginação: {}", pageable);
        Page<AlunoResponse> page = alunoRepository.findAllByAtivoTrue(pageable)
                .map(alunoMapper::toResponse);
        log.info("Total de alunos ativos encontrados: {}", page.getTotalElements());
        return page;
    }

    @Override
    public Optional<AlunoResponse> buscarAlunoPorId(Long id) {
        if (id == null) throw new NullPointerException("ID não pode ser nulo");

        if(id <= 0) throw new IllegalArgumentException("ID não pode ser nulo ou inválido");

        log.info("Buscando aluno por ID: {}", id);
        Optional<AlunoResponse> response = alunoRepository.findByIdAndAtivoTrue(id)
                .map(alunoMapper::toResponse);

        if (response.isEmpty()) {
            log.warn("Aluno não encontrado para o ID: {}", id);
        }
        return response;
    }


    @Override
    public AlunoResponse cadastrarAluno(AlunoRequest alunoRequest) {
        log.info("Verificando se já existe aluno ativo com o e-mail {} ou CPF {}", alunoRequest.email(), alunoRequest.cpf());

        Optional<Aluno> alunoComMesmoEmail = alunoRepository.findByEmailAndAtivoTrue(alunoRequest.email());
        Optional<Aluno> alunoComMesmoCpf = alunoRepository.findByCpfAndAtivoTrue(alunoRequest.cpf());

        if (alunoComMesmoEmail.isPresent()) {
            log.warn("Tentativa de cadastrar aluno com e-mail duplicado: {}", alunoRequest.email());
            throw new RuntimeException("Já existe um aluno ativo cadastrado com esse e-mail.");
        }

        if (alunoComMesmoCpf.isPresent()) {
            log.warn("Tentativa de cadastrar aluno com CPF duplicado: {}", alunoRequest.cpf());
            throw new RuntimeException("Já existe um aluno ativo cadastrado com esse CPF.");
        }

        List<Map.Entry<String, String>> campos = Arrays.asList(
                new AbstractMap.SimpleEntry<>("Nome", alunoRequest.nome()),
                new AbstractMap.SimpleEntry<>("Email", alunoRequest.email()),
                new AbstractMap.SimpleEntry<>("CPF", alunoRequest.cpf()),
                new AbstractMap.SimpleEntry<>("Curso", alunoRequest.curso()),
                new AbstractMap.SimpleEntry<>("Senha", alunoRequest.senha())
        );

        campos.forEach(campo -> {
            if (campo.getValue() == null) {
                throw new NullPointerException(campo.getKey() + " não pode ser nulo.");
            }

            if (campo.getValue().trim().isEmpty()) {
                throw new IllegalArgumentException(campo.getKey() + " não pode ser vazio.");
            }
        });

        Aluno aluno = alunoMapper.toEntity(alunoRequest);
        aluno.setSenha(passwordEncoder.encode(alunoRequest.senha()));
        aluno.setAtivo(true);

        try {
            alunoRepository.save(aluno);
            log.info("Aluno cadastrado com sucesso. ID: {}", aluno.getId());

            UserLogin userLogin = new UserLogin(alunoRequest.email(), alunoRequest.senha(), aluno);
            userLogin.setSenha(passwordEncoder.encode(alunoRequest.senha())); // Codifica a senha
            userLoginRepository.save(userLogin);  // Salva o login do aluno

            log.info("Aluno e Login cadastrados com sucesso. ID: {}", aluno.getId());
        } catch (Exception e) {
            log.error("Erro ao cadastrar aluno: {}", e.getMessage());
            throw new RuntimeException("Erro ao cadastrar aluno: " + e.getMessage(), e);
        }

        return alunoMapper.toResponse(aluno);
    }


    @Override
    public AlunoResponse atualizarAluno(Long id, AlunoRequest alunoRequest) {
        log.info("Iniciando atualização do aluno com ID: {}", id);

        if (id == null) {
            log.error("O ID do aluno não pode ser nulo.");
            throw new IllegalArgumentException("O ID do aluno não pode ser nulo.");
        }

        Aluno aluno = alunoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> {
                    log.error("❌ Aluno não encontrado ou inativo. ID: {}", id);
                    return new RuntimeException("Aluno não encontrado ou inativo.");
                });

        UserLogin userLogin = userLoginRepository.findByUsuarioAndAtivoTrue(aluno)
                .orElseThrow(() -> new RuntimeException("Login não encontrado para o Aluno"));

        boolean dadosAlterados = false;

        if (!alunoRequest.nome().equals(aluno.getNome())) {
            log.info("Atualizando nome: {} → {}", aluno.getNome(), alunoRequest.nome());
            aluno.setNome(alunoRequest.nome());
            dadosAlterados = true;
        }

        if (!alunoRequest.cpf().equals(aluno.getCpf())) {
            log.info("Atualizando CPF: {} → {}", aluno.getCpf(), alunoRequest.cpf());
            aluno.setCpf(alunoRequest.cpf());
            dadosAlterados = true;
        }

        if (!alunoRequest.curso().equals(aluno.getCurso())) {
            log.info("Atualizando curso: {} → {}", aluno.getCurso(), alunoRequest.curso());
            aluno.setCurso(alunoRequest.curso());
            dadosAlterados = true;
        }

        if (!alunoRequest.email().equals(aluno.getEmail())) {
            log.info("Verificando se já existe outro aluno ativo com o e-mail: {}", alunoRequest.email());

            Optional<Aluno> alunoComMesmoEmail = alunoRepository.findByEmailAndAtivoTrue(alunoRequest.email());
            if (alunoComMesmoEmail.isPresent() && !alunoComMesmoEmail.get().getId().equals(id)) {
                log.warn("Tentativa de atualizar para um e-mail já existente: {}", alunoRequest.email());
                throw new RuntimeException("Já existe outro aluno ativo cadastrado com esse e-mail.");
            }

            log.info("Atualizando e-mail: {} → {}", aluno.getEmail(), alunoRequest.email());
            aluno.setEmail(alunoRequest.email());
            userLogin.setEmail(alunoRequest.email());
            dadosAlterados = true;
        }

        if (alunoRequest.senha() != null && !alunoRequest.senha().trim().isEmpty()) {
            String novaSenhaCriptografada = passwordEncoder.encode(alunoRequest.senha());
            if (!novaSenhaCriptografada.equals(aluno.getSenha())) {
                log.info("Atualizando senha para o aluno ID: {}", id);
                aluno.setSenha(novaSenhaCriptografada);
                userLogin.setSenha(novaSenhaCriptografada);
                dadosAlterados = true;
            } else {
                log.info("Senha informada é igual à senha atual. Nenhuma alteração realizada.");
                throw new NenhumaAlteracaoRealizadaException();
            }
        }

        if (!dadosAlterados) {
            throw new NenhumaAlteracaoRealizadaException();
        }

        alunoRepository.save(aluno);
        userLoginRepository.save(userLogin);

        log.info("Aluno atualizado com sucesso. ID: {}", aluno.getId());
        return alunoMapper.toResponse(aluno);
    }


    @Override
    public void desativarAluno(Long id) {
        log.info("Desativando aluno com ID: {}", id);
        Aluno aluno = alunoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        userLoginRepository.findByUsuarioAndAtivoTrue(aluno).ifPresent(userLogin -> {
            if (userLogin.isAtivo()) {
                userLogin.setAtivo(false);
                userLoginRepository.save(userLogin);
                log.info("Login do aluno desativado com sucesso. ID: {}", id);
            } else {
                log.info("Login já estava inativo. Nenhuma alteração necessária.");
            }
        });

        aluno.setAtivo(false);
        alunoRepository.save(aluno);
        log.info("Aluno desativado com sucesso. ID: {}", id);
    }

    @Override
    public Optional<Aluno> findByEmail(String email) {
        if (email == null) throw new NullPointerException("Email não pode ser nulo");

        if(email.trim().isEmpty()) throw new IllegalArgumentException("Email não pode ser vazio");

        log.debug("Buscando aluno por email: {}", email);
        Optional<Aluno> alunoOpt = alunoRepository.findByEmailAndAtivoTrue(email);
        if (alunoOpt.isEmpty()) {
            log.warn("Aluno não encontrado para o email: {}", email);
        }
        return alunoOpt;
    }
}
