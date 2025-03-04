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

import java.util.*;

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

        // ⚠️ Verifica se já existe um administrador ativo com o mesmo e-mail ou CPF
        if (administradorRepository.findByEmailAndAtivoTrue(administradorRequest.email()).isPresent()) {
            log.warn("Tentativa de cadastrar administrador com e-mail já existente: {}", administradorRequest.email());
            throw new RuntimeException("Já existe um administrador ativo cadastrado com esse e-mail.");
        }

        if (administradorRepository.findByCpfAndAtivoTrue(administradorRequest.cpf()).isPresent()) {
            log.warn("Tentativa de cadastrar administrador com CPF já existente: {}", administradorRequest.cpf());
            throw new RuntimeException("Já existe um administrador ativo cadastrado com esse CPF.");
        }

        // 🚨 Validação de campos nulos ou vazios diretamente no cadastrarAdministrador
        // Lista com todos os campos que precisam ser validados e seus respectivos nomes
        List<Map.Entry<String, String>> campos = Arrays.asList(
                new AbstractMap.SimpleEntry<>("Nome", administradorRequest.nome()),
                new AbstractMap.SimpleEntry<>("Email", administradorRequest.email()),
                new AbstractMap.SimpleEntry<>("CPF", administradorRequest.cpf()),
                new AbstractMap.SimpleEntry<>("Setor", administradorRequest.setor()),
                new AbstractMap.SimpleEntry<>("Senha", administradorRequest.senha()),
                new AbstractMap.SimpleEntry<>("SIAPE", administradorRequest.siape())
        );

        // Validação dos campos
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
        admin.setAtivo(true);

        try {
            administradorRepository.save(admin);
            log.info("Administrador cadastrado com sucesso. ID: {}", admin.getId());
        } catch (Exception e) {
            log.error("Erro ao cadastrar administrador: {}", e.getMessage());
            throw new RuntimeException("Erro ao cadastrar administrador: " + e.getMessage(), e);
        }

        return administradorMapper.toResponse(admin);
    }

    @Override
    public AdministradorResponse atualizarAdministrador(Long id, AdministradorRequest administradorRequest) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID não pode ser nulo ou inválido");
        }

        log.info("Atualizando administrador com ID: {}", id);

        Administrador admin = administradorRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> {
                    log.error("Administrador não encontrado ou inativo para o ID: {}", id);
                    return new RuntimeException("Administrador não encontrado ou inativo.");
                });

        // ✅ Atualiza apenas os campos informados
        if (administradorRequest.nome() != null) {
            admin.setNome(administradorRequest.nome());
        }
        if (administradorRequest.email() != null) {
            admin.setEmail(administradorRequest.email());
        }
        if (administradorRequest.cpf() != null) {
            admin.setCpf(administradorRequest.cpf());
        }
        if (administradorRequest.setor() != null) {
            admin.setSetor(administradorRequest.setor());
        }
        if (administradorRequest.siape() != null) {
            admin.setSiape(administradorRequest.siape());
        }

        // ✅ Se a senha for informada, atualiza. Caso contrário, mantém a anterior
        if (administradorRequest.senha() != null && !administradorRequest.senha().trim().isEmpty()) {
            admin.setSenha(passwordEncoder.encode(administradorRequest.senha()));
            log.info("Senha atualizada para o administrador ID: {}", id);
        } else {
            log.info("Nenhuma senha informada. Mantendo a senha antiga para o administrador ID: {}", id);
        }

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
