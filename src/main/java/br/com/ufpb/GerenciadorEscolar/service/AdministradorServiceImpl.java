package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.model.dto.administrador.AdministradorRequest;
import br.com.ufpb.GerenciadorEscolar.model.dto.administrador.AdministradorResponse;
import br.com.ufpb.GerenciadorEscolar.mapper.AdministradorMapper;
import br.com.ufpb.GerenciadorEscolar.model.entity.Administrador;
import br.com.ufpb.GerenciadorEscolar.model.entity.UserLogin;
import br.com.ufpb.GerenciadorEscolar.repository.AdministradorRepository;
import br.com.ufpb.GerenciadorEscolar.repository.UserLoginRepository;
import br.com.ufpb.GerenciadorEscolar.util.UsuarioUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AdministradorServiceImpl implements AdministradorServiceInterface {

    private final UsuarioUtils usuarioUtils;
    private final AdministradorRepository administradorRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdministradorMapper administradorMapper;
    private final UserLoginRepository userLoginRepository;

    public AdministradorServiceImpl(AdministradorRepository administradorRepository,
                                    PasswordEncoder passwordEncoder,
                                    AdministradorMapper administradorMapper,
                                    UserLoginRepository userLoginRepository,
                                    UsuarioUtils usuarioUtils) {
        this.administradorRepository = administradorRepository;
        this.passwordEncoder = passwordEncoder;
        this.administradorMapper = administradorMapper;
        this.userLoginRepository = userLoginRepository;
        this.usuarioUtils = usuarioUtils;
    }

    /**
     * Cadastrar um novo administrador.
     *
     * Este método verifica se o administrador já existe no banco de dados, valida suas credenciais
     * e realiza o cadastro do administrador e seu login no sistema.
     *
     * @param administradorRequest - Objeto contendo os dados do administrador a ser cadastrado.
     * @return AdministradorResponse - Retorna os dados do administrador cadastrado no formato de resposta.
     * @throws EmailJaCadastradoException - Se já existir um administrador com o mesmo e-mail.
     * @throws CpfJaCadastradoException - Se já existir um administrador com o mesmo CPF.
     * @throws SiapeJaCadastradoException - Se já existir um administrador com o mesmo SIAPE.
     */
    @Override
    public AdministradorResponse cadastrarAdministrador(AdministradorRequest administradorRequest) {
        log.info("Iniciando cadastro de administrador: {}", administradorRequest.email());

        if (administradorRepository.findByEmailAndAtivoTrue(administradorRequest.email()).isPresent()) {
            throw new EmailJaCadastradoException("Já existe um administrador ativo cadastrado com esse e-mail.");
        }

        if (administradorRepository.findByCpfAndAtivoTrue(administradorRequest.cpf()).isPresent()) {
            throw new CpfJaCadastradoException("Já existe um administrador ativo cadastrado com esse CPF.");
        }

        if (administradorRepository.findBySiapeAndAtivoTrue(administradorRequest.siape()).isPresent()) {
            throw new SiapeJaCadastradoException("Já existe um administrador ativo cadastrado com esse SIAPE.");
        }

        Administrador admin = administradorMapper.toEntity(administradorRequest);
        admin.setSenha(passwordEncoder.encode(administradorRequest.senha()));

        administradorRepository.save(admin);
        log.info("Administrador cadastrado com sucesso. ID: {}", admin.getId());

        UserLogin userLogin = new UserLogin();
        userLogin.setEmail(admin.getEmail());
        userLogin.setUsuario(admin);
        userLogin.setSenha(passwordEncoder.encode(administradorRequest.senha()));
        userLoginRepository.save(userLogin);
        log.info("Administrador e Login cadastrados com sucesso. ID: {}", admin.getId());

        return administradorMapper.toResponse(admin);
    }

    /**
     * Listar administradores ativos com paginação.
     *
     * Este método retorna uma lista paginada de administradores que estão ativos no sistema.
     *
     * @param pageable - Objeto `Pageable` contendo as informações de paginação.
     * @return Page<AdministradorResponse> - Retorna uma página contendo os administradores ativos.
     */
    @Override
    public Page<AdministradorResponse> listarAdministradoresAtivos(Pageable pageable) {
        log.info("Listando administradores ativos com paginação: {}", pageable);
        return administradorRepository.findAllByAtivoTrue(pageable)
                .map(administradorMapper::toResponse);
    }

    /**
     * Buscar um administrador pelo ID.
     *
     * Este método recupera um administrador ativo no sistema a partir do seu ID.
     *
     * @param id - ID do administrador a ser buscado.
     * @return AdministradorResponse - Retorna os dados do administrador no formato de resposta.
     * @throws AdministradorNaoEncontradoException - Se o administrador não for encontrado.
     */
    @Override
    public AdministradorResponse buscarAdministradorPorId(Long id) {
        log.info("Buscando administrador por ID: {}", id);
        Administrador admin = administradorRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> {
                    log.warn("Administrador não encontrado para o ID: {}", id);
                    return new AdministradorNaoEncontradoException("Administrador não encontrado.");
                });

        return administradorMapper.toResponse(admin);
    }

    /**
     * Atualizar as informações de um administrador.
     *
     * Este método permite atualizar os dados de um administrador ativo no sistema, garantindo a
     * consistência dos dados e verificando a duplicidade de informações sensíveis como e-mail, CPF e SIAPE.
     *
     * @param id - ID do administrador que será atualizado.
     * @param administradorRequest - Objeto contendo os novos dados do administrador.
     * @return AdministradorResponse - Retorna os dados do administrador atualizado no formato de resposta.
     * @throws AdministradorNaoEncontradoException - Se o administrador não for encontrado ou estiver inativo.
     * @throws NenhumaAlteracaoRealizadaException - Se nenhuma alteração foi feita nos dados do administrador.
     */
    @Override
    public AdministradorResponse atualizarAdministrador(Long id, AdministradorRequest administradorRequest) {
        log.info("Atualizando administrador com ID: {}", id);

        Administrador admin = administradorRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new AdministradorNaoEncontradoException("Administrador não encontrado ou inativo."));

        UserLogin userLogin = userLoginRepository.findByUsuarioAndAtivoTrue(admin)
                .orElseThrow(() -> new LoginNaoEncontradoException("Login não encontrado para o Administrador"));

        boolean dadosAlterados = usuarioUtils.atualizarDadosUsuario(
                admin,
                userLogin,
                administradorRequest.nome(),
                administradorRequest.email(),
                administradorRequest.cpf(),
                administradorRequest.senha(),
                passwordEncoder
        );

        if (administradorRequest.setor() != null && !administradorRequest.setor().equals(admin.getSetor())) {
            admin.setSetor(administradorRequest.setor());
            dadosAlterados = true;
        }

        if (administradorRequest.siape() != null && !administradorRequest.siape().equals(admin.getSiape())) {
            admin.setSiape(administradorRequest.siape());
            dadosAlterados = true;
        }

        if (!dadosAlterados) {
            throw new NenhumaAlteracaoRealizadaException();
        }

        administradorRepository.save(admin);
        userLoginRepository.save(userLogin);

        log.info("Administrador atualizado com sucesso. ID: {}", admin.getId());
        return administradorMapper.toResponse(admin);
    }

    /**
     * Desativar um administrador no sistema.
     *
     * Este método desativa um administrador e seu login, impedindo o acesso ao sistema.
     *
     * @param id - ID do administrador a ser desativado.
     * @throws AdministradorNaoEncontradoException - Se o administrador não for encontrado.
     */
    @Override
    public void desativarAdministrador(Long id) {
        log.info("Desativando administrador com ID: {}", id);

        Administrador admin = administradorRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new AdministradorNaoEncontradoException("Administrador não encontrado"));

        userLoginRepository.findByUsuarioAndAtivoTrue(admin).ifPresent(userLogin -> {
            userLogin.setAtivo(false);
            userLoginRepository.save(userLogin);
            log.info("Login do administrador desativado. ID: {}", id);
        });

        admin.setAtivo(false);
        administradorRepository.save(admin);
        log.info("Administrador desativado. ID: {}", id);
    }
}
