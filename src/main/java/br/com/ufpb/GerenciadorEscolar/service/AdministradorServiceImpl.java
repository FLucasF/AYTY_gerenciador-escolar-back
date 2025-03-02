package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorRequest;
import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorResponse;
import br.com.ufpb.GerenciadorEscolar.mapper.AdministradorMapper;
import br.com.ufpb.GerenciadorEscolar.model.Administrador;
import br.com.ufpb.GerenciadorEscolar.repository.AdministradorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class AdministradorServiceImpl implements AdministradorServiceInterface {

    private final AdministradorRepository administradorRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdministradorMapper administradorMapper;

    @Autowired
    public AdministradorServiceImpl(AdministradorRepository administradorRepository,
                                    PasswordEncoder passwordEncoder,
                                    AdministradorMapper administradorMapper) {
        this.administradorRepository = administradorRepository;
        this.passwordEncoder = passwordEncoder;
        this.administradorMapper = administradorMapper;
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
        Optional<AdministradorResponse> response = administradorRepository.findByIdAndAtivoTrue(id)
                .map(administradorMapper::toResponse);
        if (response.isEmpty()) {
            log.warn("Administrador não encontrado para o ID: {}", id);
        } else {
            log.debug("Administrador encontrado: {}", response.get());
        }
        return response;
    }

    @Override
    public AdministradorResponse cadastrarAdministrador(AdministradorRequest administradorRequest) {
        log.info("Iniciando cadastro de administrador com dados: {}", administradorRequest);
        Administrador admin = administradorMapper.toEntity(administradorRequest);
        admin.setSenha(passwordEncoder.encode(admin.getSenha()));
        admin.setAtivo(true);

        try {
            administradorRepository.save(admin);
            log.info("Administrador cadastrado com sucesso. ID: {}", admin.getId());
        } catch (Exception e) {
            log.error("Erro ao cadastrar administrador: {}", e.getMessage());
            throw new RuntimeException("Erro ao cadastrar administrador: " + e.getMessage(), e);
        }
        log.debug("Role do administrador cadastrado: {}", admin.getRole());
        return administradorMapper.toResponse(admin);
    }

    @Override
    public AdministradorResponse atualizarAdministrador(Long id, AdministradorRequest administradorRequest) {
        log.info("Atualizando administrador com ID: {}", id);
        Administrador admin = administradorRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Administrador não encontrado para o ID: {}", id);
                    return new RuntimeException("Administrador não encontrado");
                });

        admin.setNome(administradorRequest.nome());
        admin.setEmail(administradorRequest.email());
        admin.setSetor(administradorRequest.setor());

        administradorRepository.save(admin);
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
        admin.setAtivo(false);
        administradorRepository.save(admin);
        log.info("Administrador desativado com sucesso. ID: {}", id);
    }

    @Override
    public Optional<Administrador> findByEmail(String email) {
        log.debug("Buscando administrador por email: {}", email);
        Optional<Administrador> adminOpt = administradorRepository.findByEmailAndAtivoTrue(email);
        if (adminOpt.isEmpty()) {
            log.warn("Administrador não encontrado para o email: {}", email);
        }
        return adminOpt;
    }
}
