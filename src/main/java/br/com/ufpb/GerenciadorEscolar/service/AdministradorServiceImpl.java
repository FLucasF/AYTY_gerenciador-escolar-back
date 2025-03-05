package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorRequest;
import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorResponse;
import br.com.ufpb.GerenciadorEscolar.mapper.AdministradorMapper;
import br.com.ufpb.GerenciadorEscolar.model.Administrador;
import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.model.UserLogin;
import br.com.ufpb.GerenciadorEscolar.repository.AdministradorRepository;
import br.com.ufpb.GerenciadorEscolar.repository.UserLoginRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
@Slf4j
public class AdministradorServiceImpl implements AdministradorServiceInterface {

    private final AdministradorRepository administradorRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdministradorMapper administradorMapper;
    private final UserLoginRepository userLoginRepository;

    @Autowired
    public AdministradorServiceImpl(AdministradorRepository administradorRepository,
                                    PasswordEncoder passwordEncoder,
                                    AdministradorMapper administradorMapper,
                                    UserLoginRepository userLoginRepository) {
        this.administradorRepository = administradorRepository;
        this.passwordEncoder = passwordEncoder;
        this.administradorMapper = administradorMapper;
        this.userLoginRepository = userLoginRepository;
    }

    @Override
    public Page<AdministradorResponse> listarAdministradoresAtivos(Pageable pageable) {
        log.info("Listando administradores ativos com paginação: {}", pageable);
        Page<AdministradorResponse> page = administradorRepository.findAllByAtivoTrue(pageable)
                .map(administradorMapper::toResponse);
        log.info("Total de administradores ativos encontrados: {}", page.getTotalElements());
        return page;
    }

    @Override
    public Optional<AdministradorResponse> buscarAdministradorPorId(Long id) {
        log.info("Buscando administrador por ID: {}", id);
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID não pode ser nulo ou inválido");
        }

        Optional<AdministradorResponse> response = administradorRepository.findByIdAndAtivoTrue(id)
                .map(administradorMapper::toResponse);
        if (response.isEmpty()) {
            log.warn("Administrador não encontrado para o ID: {}", id);
        }
        return response;
    }


    @Override
    public AdministradorResponse cadastrarAdministrador(AdministradorRequest administradorRequest) {
        log.info("Iniciando cadastro de administrador: {}", administradorRequest);

        if (administradorRepository.findByEmailAndAtivoTrue(administradorRequest.email()).isPresent()) {
            log.warn("Tentativa de cadastrar administrador com e-mail já existente: {}", administradorRequest.email());
            throw new RuntimeException("Já existe um administrador ativo cadastrado com esse e-mail.");
        }

        if (administradorRepository.findByCpfAndAtivoTrue(administradorRequest.cpf()).isPresent()) {
            log.warn("Tentativa de cadastrar administrador com CPF já existente: {}", administradorRequest.cpf());
            throw new RuntimeException("Já existe um administrador ativo cadastrado com esse CPF.");
        }

        List<Map.Entry<String, String>> campos = Arrays.asList(
                new AbstractMap.SimpleEntry<>("Nome", administradorRequest.nome()),
                new AbstractMap.SimpleEntry<>("Email", administradorRequest.email()),
                new AbstractMap.SimpleEntry<>("CPF", administradorRequest.cpf()),
                new AbstractMap.SimpleEntry<>("Setor", administradorRequest.setor()),
                new AbstractMap.SimpleEntry<>("Senha", administradorRequest.senha()),
                new AbstractMap.SimpleEntry<>("SIAPE", administradorRequest.siape())
        );

        campos.forEach(campo -> {
            if (campo.getValue() == null) {
                throw new NullPointerException(campo.getKey() + " não pode ser nulo.");
            }

            if (campo.getValue().trim().isEmpty()) {
                throw new IllegalArgumentException(campo.getKey() + " não pode ser vazio.");
            }
        });

        Administrador admin = administradorMapper.toEntity(administradorRequest);
        admin.setSenha(passwordEncoder.encode(administradorRequest.senha()));

        try {
            administradorRepository.save(admin);
            log.info("Administrador cadastrado com sucesso. ID: {}", admin.getId());

            UserLogin userLogin = new UserLogin(administradorRequest.email(), administradorRequest.senha(), admin);
            userLogin.setSenha(passwordEncoder.encode(administradorRequest.senha()));
            userLoginRepository.save(userLogin);

            log.info("Administrador e Login cadastrados com sucesso. ID: {}", admin.getId());

            log.info("Administrador e Login cadastrados com sucesso. ID: {}", admin.getId());
        } catch (Exception e) {
            log.error("Erro ao cadastrar administrador: {}", e.getMessage());
            throw new RuntimeException("Erro ao cadastrar administrador: " + e.getMessage(), e);
        }

        return administradorMapper.toResponse(admin);
    }

    @Override
    public AdministradorResponse atualizarAdministrador(Long id, AdministradorRequest administradorRequest) {
        log.info(administradorRequest.toString());

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID não pode ser nulo ou inválido");
        }

        log.info("Atualizando administrador com ID: {}", id);

        Administrador admin = administradorRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> {
                    log.error("Administrador não encontrado ou inativo para o ID: {}", id);
                    return new RuntimeException("Administrador não encontrado ou inativo.");
                });

        UserLogin userLogin = userLoginRepository.findByUsuarioAndAtivoTrue(admin)
                .orElseThrow(() -> new RuntimeException("Login não encontrado para o Administrador"));

        boolean dadosAlterados = false;

        if (!administradorRequest.nome().equals(admin.getNome())) {
            admin.setNome(administradorRequest.nome());
            dadosAlterados = true;
        }

        if (!administradorRequest.cpf().equals(admin.getCpf())) {
            admin.setCpf(administradorRequest.cpf());
            dadosAlterados = true;
        }

        if (!administradorRequest.setor().equals(admin.getSetor())) {
            admin.setSetor(administradorRequest.setor());
            dadosAlterados = true;
        }

        if (!administradorRequest.siape().equals(admin.getSiape())) {
            admin.setSiape(administradorRequest.siape());
            dadosAlterados = true;
        }

        if (!administradorRequest.email().equals(admin.getEmail())) {
            admin.setEmail(administradorRequest.email());
            userLogin.setEmail(administradorRequest.email());
            dadosAlterados = true;
        }

        if (administradorRequest.senha() != null && !administradorRequest.senha().trim().isEmpty()) {
            String novaSenhaCriptografada = passwordEncoder.encode(administradorRequest.senha());
            if (!novaSenhaCriptografada.equals(admin.getSenha())) {
                admin.setSenha(novaSenhaCriptografada);
                userLogin.setSenha(novaSenhaCriptografada);
                dadosAlterados = true;
                log.info("Senha atualizada para o administrador ID: {}", id);
            } else {
                log.info("Senha informada é igual à senha atual. Nenhuma alteração realizada.");
                throw new NenhumaAlteracaoRealizadaException();
            }
        }

        if (!dadosAlterados) {
            throw new NenhumaAlteracaoRealizadaException();
        }

        administradorRepository.save(admin);
        userLoginRepository.save(userLogin);

        log.info("Administrador atualizado com sucesso. ID: {}", admin.getId());
        return administradorMapper.toResponse(admin);
    }





    @Override
    public void desativarAdministrador(Long id) {
        log.info("Desativando administrador com ID: {}", id);

        Administrador admin = administradorRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> {
                    log.error("Administrador não encontrado para desativação com ID: {}", id);
                    return new RuntimeException("Administrador não encontrado");
                });

        // Desativa o login caso exista e esteja ativo
        userLoginRepository.findByUsuarioAndAtivoTrue(admin).ifPresent(userLogin -> {
            if (userLogin.isAtivo()) {
                userLogin.setAtivo(false);
                userLoginRepository.save(userLogin);
                log.info("Login do administrador desativado com sucesso. ID: {}", id);
            } else {
                log.info("Login do administrador já estava inativo. Nenhuma alteração necessária. ID: {}", id);
            }
        });

        // Desativar o administrador
        admin.setAtivo(false);
        administradorRepository.save(admin);
        log.info("Administrador desativado com sucesso. ID: {}", id);
    }



    @Override
    public Optional<Administrador> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email não pode ser nulo ou vazio");
        }

        log.debug("Buscando administrador por email: {}", email);
        Optional<Administrador> adminOpt = administradorRepository.findByEmailAndAtivoTrue(email);

        if (adminOpt.isEmpty()) {
            log.warn("Administrador não encontrado para o email: {}", email);
        }

        return adminOpt;
    }

}
