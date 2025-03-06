package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoRequest;
import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoResponse;
import br.com.ufpb.GerenciadorEscolar.mapper.AlunoMapper;
import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.model.UserLogin;
import br.com.ufpb.GerenciadorEscolar.repository.AlunoRepository;
import br.com.ufpb.GerenciadorEscolar.repository.UserLoginRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class AlunoServiceImpl implements AlunoServiceInterface {

    private final AlunoRepository alunoRepository;
    private final PasswordEncoder passwordEncoder;
    private final AlunoMapper alunoMapper;
    private final UserLoginRepository userLoginRepository;

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
    public AlunoResponse cadastrarAluno(AlunoRequest alunoRequest) {
        log.info("Verificando se já existe aluno ativo com o e-mail {} ou CPF {}", alunoRequest.email(), alunoRequest.cpf());

        if (alunoRepository.findByEmailAndAtivoTrue(alunoRequest.email()).isPresent()) {
            throw new EmailJaCadastradoException("Já existe um aluno ativo cadastrado com esse e-mail.");
        }

        if (alunoRepository.findByCpfAndAtivoTrue(alunoRequest.cpf()).isPresent()) {
            throw new CpfJaCadastradoException("Já existe um aluno ativo cadastrado com esse CPF.");
        }

        Aluno aluno = alunoMapper.toEntity(alunoRequest);
        aluno.setSenha(passwordEncoder.encode(alunoRequest.senha()));

        alunoRepository.save(aluno);
        log.info("Aluno cadastrado com sucesso. ID: {}", aluno.getId());

        UserLogin userLogin = new UserLogin();
        userLogin.setEmail(aluno.getEmail());
        userLogin.setUsuario(aluno);
        userLogin.setSenha(passwordEncoder.encode(alunoRequest.senha()));
        userLoginRepository.save(userLogin);

        log.info("Aluno e Login cadastrados com sucesso. ID: {}", aluno.getId());
        return alunoMapper.toResponse(aluno);
    }

    @Override
    public Page<AlunoResponse> listarAlunosAtivos(Pageable pageable) {
        log.info("Listando alunos ativos com paginação: {}", pageable);
        return alunoRepository.findAllByAtivoTrue(pageable)
                .map(alunoMapper::toResponse);
    }

    @Override
    public AlunoResponse buscarAlunoPorId(Long id) {
        log.info("Buscando aluno por ID: {}", id);

        Aluno aluno = alunoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> {
                    log.warn("Aluno não encontrado para o ID: {}", id);
                    return new AlunoNaoEncontradoException("Aluno não encontrado.");
                });

        return alunoMapper.toResponse(aluno);
    }



    @Override
    public AlunoResponse atualizarAluno(Long id, AlunoRequest alunoRequest) {
        log.info("Atualizando aluno com ID: {}", id);

        Aluno aluno = alunoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new AlunoNaoEncontradoException("Aluno não encontrado ou inativo."));

        UserLogin userLogin = userLoginRepository.findByUsuarioAndAtivoTrue(aluno)
                .orElseThrow(() -> new AlunoNaoEncontradoException("Login não encontrado para o Aluno"));

        boolean dadosAlterados = false;
        boolean loginAlterado = false;  // ✅ Verifica se precisa atualizar o UserLogin

        if (!alunoRequest.nome().equals(aluno.getNome())) {
            aluno.setNome(alunoRequest.nome());
            dadosAlterados = true;
        }

        if (!alunoRequest.cpf().equals(aluno.getCpf())) {
            aluno.setCpf(alunoRequest.cpf());
            dadosAlterados = true;
        }

        if (!alunoRequest.curso().equals(aluno.getCurso())) {
            aluno.setCurso(alunoRequest.curso());
            dadosAlterados = true;
        }

        if (!alunoRequest.email().equals(aluno.getEmail())) {
            if (alunoRepository.findByEmailAndAtivoTrue(alunoRequest.email()).isPresent()) {
                throw new EmailJaCadastradoException("Já existe outro aluno ativo cadastrado com esse e-mail.");
            }
            aluno.setEmail(alunoRequest.email());
            userLogin.setEmail(alunoRequest.email()); // ✅ Atualiza o email no UserLogin
            dadosAlterados = true;
            loginAlterado = true; // ✅ Marca que o login precisa ser salvo
        }

        if (alunoRequest.senha() != null && !alunoRequest.senha().trim().isEmpty()) {
            if (passwordEncoder.matches(alunoRequest.senha(), aluno.getSenha())) {
                throw new NenhumaAlteracaoRealizadaException();
            }

            aluno.setSenha(passwordEncoder.encode(alunoRequest.senha()));
            userLogin.setSenha(passwordEncoder.encode(alunoRequest.senha())); // ✅ Atualiza a senha no UserLogin
            dadosAlterados = true;
            loginAlterado = true; // ✅ Marca que o login precisa ser salvo
        }

        if (!dadosAlterados) {
            throw new NenhumaAlteracaoRealizadaException();
        }

        alunoRepository.save(aluno);

        // ✅ Só salva o UserLogin se email ou senha forem alterados
        if (loginAlterado) {
            userLoginRepository.save(userLogin);
        }

        log.info("Aluno atualizado com sucesso. ID: {}", aluno.getId());
        return alunoMapper.toResponse(aluno);
    }


    @Override
    public void desativarAluno(Long id) {
        log.info("Desativando aluno com ID: {}", id);

        Aluno aluno = alunoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new AlunoNaoEncontradoException("Aluno não encontrado"));

        userLoginRepository.findByUsuarioAndAtivoTrue(aluno).ifPresent(userLogin -> {
            userLogin.setAtivo(false);
            userLoginRepository.save(userLogin);
            log.info("Login do aluno desativado. ID: {}", id);
        });

        aluno.setAtivo(false);
        alunoRepository.save(aluno);
        log.info("Aluno desativado. ID: {}", id);
    }
}
