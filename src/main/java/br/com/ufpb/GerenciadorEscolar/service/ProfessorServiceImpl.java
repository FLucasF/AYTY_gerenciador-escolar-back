package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.model.dto.professor.ProfessorRequest;
import br.com.ufpb.GerenciadorEscolar.model.dto.professor.ProfessorResponse;
import br.com.ufpb.GerenciadorEscolar.mapper.ProfessorMapper;
import br.com.ufpb.GerenciadorEscolar.model.entity.Professor;
import br.com.ufpb.GerenciadorEscolar.model.entity.UserLogin;
import br.com.ufpb.GerenciadorEscolar.repository.ProfessorRepository;
import br.com.ufpb.GerenciadorEscolar.repository.UserLoginRepository;
import br.com.ufpb.GerenciadorEscolar.util.UsuarioUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProfessorServiceImpl implements ProfessorServiceInterface {

    private final ProfessorRepository professorRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfessorMapper professorMapper;
    private final UserLoginRepository userLoginRepository;
    private final UsuarioUtils usuarioUtils;

    public ProfessorServiceImpl(ProfessorRepository professorRepository,
                                PasswordEncoder passwordEncoder,
                                ProfessorMapper professorMapper,
                                UserLoginRepository userLoginRepository, UsuarioUtils usuarioUtils) {
        this.professorRepository = professorRepository;
        this.passwordEncoder = passwordEncoder;
        this.professorMapper = professorMapper;
        this.userLoginRepository = userLoginRepository;
        this.usuarioUtils = usuarioUtils;
    }

    /**
     * Cadastrar um novo professor.
     *
     * Este método verifica se o professor já existe no banco de dados, valida suas credenciais
     * e realiza o cadastro do professor e seu login no sistema.
     *
     * @param professorRequest - Objeto contendo os dados do professor a ser cadastrado.
     * @return ProfessorResponse - Retorna os dados do professor cadastrado no formato de resposta.
     * @throws EmailJaCadastradoException - Se já existir um professor com o mesmo e-mail.
     * @throws CpfJaCadastradoException - Se já existir um professor com o mesmo CPF.
     * @throws SiapeJaCadastradoException - Se já existir um professor com o mesmo SIAPE.
     */
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

    /**
     * Listar professores ativos com paginação.
     *
     * Este método retorna uma lista paginada de professores que estão ativos no sistema.
     *
     * @param pageable - Objeto `Pageable` contendo as informações de paginação.
     * @return Page<ProfessorResponse> - Retorna uma página contendo os professores ativos.
     */
    @Override
    public Page<ProfessorResponse> listarProfessoresAtivos(Pageable pageable) {
        log.info("Listando professores ativos com paginação: {}", pageable);
        return professorRepository.findAllByAtivoTrue(pageable)
                .map(professorMapper::toResponse);
    }

    /**
     * Buscar um professor pelo ID.
     *
     * Este método recupera um professor ativo no sistema a partir do seu ID.
     *
     * @param id - ID do professor a ser buscado.
     * @return ProfessorResponse - Retorna os dados do professor no formato de resposta.
     * @throws ProfessorNaoEncontradoException - Se o professor não for encontrado.
     */
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

    /**
     * Atualizar as informações de um professor.
     *
     * Este método permite atualizar os dados de um professor ativo no sistema, garantindo a
     * consistência dos dados e verificando a duplicidade de informações sensíveis como e-mail, CPF e SIAPE.
     *
     * @param id - ID do professor que será atualizado.
     * @param professorRequest - Objeto contendo os novos dados do professor.
     * @return ProfessorResponse - Retorna os dados do professor atualizado no formato de resposta.
     * @throws ProfessorNaoEncontradoException - Se o professor não for encontrado ou estiver inativo.
     * @throws SiapeJaCadastradoException - Se o SIAPE já estiver cadastrado para outro professor ativo.
     * @throws NenhumaAlteracaoRealizadaException - Se nenhuma alteração foi feita nos dados do professor.
     */
    @Override
    public ProfessorResponse atualizarProfessor(Long id, ProfessorRequest professorRequest) {
        log.info("Atualizando professor com ID: {}", id);

        Professor professor = professorRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ProfessorNaoEncontradoException("Professor não encontrado ou inativo."));

        UserLogin userLogin = userLoginRepository.findByUsuarioAndAtivoTrue(professor)
                .orElseThrow(() -> new LoginNaoEncontradoException("Login não encontrado para o Professor"));

        boolean dadosAlterados = usuarioUtils.atualizarDadosUsuario(
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


    /**
     * Desativar um professor no sistema.
     *
     * Este método desativa um professor e seu login, impedindo o acesso ao sistema.
     *
     * @param id - ID do professor a ser desativado.
     * @throws ProfessorNaoEncontradoException - Se o professor não for encontrado.
     */
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
