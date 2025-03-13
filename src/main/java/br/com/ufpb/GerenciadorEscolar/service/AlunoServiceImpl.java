package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.model.dto.aluno.AlunoRequest;
import br.com.ufpb.GerenciadorEscolar.model.dto.aluno.AlunoResponse;
import br.com.ufpb.GerenciadorEscolar.mapper.AlunoMapper;
import br.com.ufpb.GerenciadorEscolar.model.entity.Aluno;
import br.com.ufpb.GerenciadorEscolar.model.entity.UserLogin;
import br.com.ufpb.GerenciadorEscolar.repository.AlunoRepository;
import br.com.ufpb.GerenciadorEscolar.repository.UserLoginRepository;
import br.com.ufpb.GerenciadorEscolar.util.UsuarioUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AlunoServiceImpl implements AlunoServiceInterface {

    private final UsuarioUtils usuarioUtils;
    private final AlunoRepository alunoRepository;
    private final PasswordEncoder passwordEncoder;
    private final AlunoMapper alunoMapper;
    private final UserLoginRepository userLoginRepository;

    public AlunoServiceImpl(AlunoRepository alunoRepository,
                            PasswordEncoder passwordEncoder,
                            AlunoMapper alunoMapper,
                            UserLoginRepository userLoginRepository,
                            UsuarioUtils usuarioUtils) {
        this.alunoRepository = alunoRepository;
        this.passwordEncoder = passwordEncoder;
        this.alunoMapper = alunoMapper;
        this.userLoginRepository = userLoginRepository;
        this.usuarioUtils = usuarioUtils;
    }

    /**
     * Cadastrar um novo aluno.
     *
     * Este método verifica se o aluno já existe no banco de dados, valida suas credenciais
     * e realiza o cadastro do aluno e seu login no sistema.
     *
     * @param alunoRequest - Objeto contendo os dados do aluno a ser cadastrado.
     * @return AlunoResponse - Retorna os dados do aluno cadastrado no formato de resposta.
     * @throws EmailJaCadastradoException - Se já existir um aluno com o mesmo e-mail.
     * @throws CpfJaCadastradoException - Se já existir um aluno com o mesmo CPF.
     */
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

    /**
     * Listar alunos ativos com paginação.
     *
     * Este método retorna uma lista paginada de alunos que estão ativos no sistema.
     *
     * @param pageable - Objeto `Pageable` contendo as informações de paginação.
     * @return Page<AlunoResponse> - Retorna uma página contendo os alunos ativos.
     */
    @Override
    public Page<AlunoResponse> listarAlunosAtivos(Pageable pageable) {
        log.info("Listando alunos ativos com paginação: {}", pageable);
        return alunoRepository.findAllByAtivoTrue(pageable)
                .map(alunoMapper::toResponse);
    }

    /**
     * Buscar um aluno pelo ID.
     *
     * Este método recupera um aluno ativo no sistema a partir do seu ID.
     *
     * @param id - ID do aluno a ser buscado.
     * @return AlunoResponse - Retorna os dados do aluno no formato de resposta.
     * @throws AlunoNaoEncontradoException - Se o aluno não for encontrado.
     */
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

    /**
     * Atualizar as informações de um aluno.
     *
     * Este método permite atualizar os dados de um aluno ativo no sistema, garantindo a
     * consistência dos dados e verificando a duplicidade de informações sensíveis como e-mail e CPF.
     *
     * @param id - ID do aluno que será atualizado.
     * @param alunoRequest - Objeto contendo os novos dados do aluno.
     * @return AlunoResponse - Retorna os dados do aluno atualizado no formato de resposta.
     * @throws AlunoNaoEncontradoException - Se o aluno não for encontrado ou estiver inativo.
     * @throws NenhumaAlteracaoRealizadaException - Se nenhuma alteração foi feita nos dados do aluno.
     */
    @Override
    public AlunoResponse atualizarAluno(Long id, AlunoRequest alunoRequest) {
        log.info("Atualizando aluno com ID: {}", id);

        Aluno aluno = alunoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new AlunoNaoEncontradoException("Aluno não encontrado ou inativo."));

        UserLogin userLogin = userLoginRepository.findByUsuarioAndAtivoTrue(aluno)
                .orElseThrow(() -> new LoginNaoEncontradoException("Login não encontrado para o Aluno"));

        boolean dadosAlterados = usuarioUtils.atualizarDadosUsuario(
                aluno,
                userLogin,
                alunoRequest.nome(),
                alunoRequest.email(),
                alunoRequest.cpf(),
                alunoRequest.senha(),
                passwordEncoder
        );

        if (alunoRequest.curso() != null && !alunoRequest.curso().equals(aluno.getCurso())) {
            aluno.setCurso(alunoRequest.curso());
            dadosAlterados = true;
        }

        if (!dadosAlterados) {
            throw new NenhumaAlteracaoRealizadaException();
        }

        alunoRepository.save(aluno);
        userLoginRepository.save(userLogin);

        log.info("Aluno atualizado com sucesso. ID: {}", aluno.getId());
        return alunoMapper.toResponse(aluno);
    }

    /**
     * Desativar um aluno no sistema.
     *
     * Este método desativa um aluno e seu login, impedindo o acesso ao sistema.
     *
     * @param id - ID do aluno a ser desativado.
     * @throws AlunoNaoEncontradoException - Se o aluno não for encontrado.
     */
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
