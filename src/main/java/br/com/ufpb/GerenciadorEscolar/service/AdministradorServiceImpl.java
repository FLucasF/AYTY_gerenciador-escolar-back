package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorRequest;
import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorResponse;
import br.com.ufpb.GerenciadorEscolar.mapper.AdministradorMapper;
import br.com.ufpb.GerenciadorEscolar.model.Administrador;
import br.com.ufpb.GerenciadorEscolar.repository.AdministradorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
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
        return administradorRepository.findAllByAtivoTrue(pageable)
                .map(administradorMapper::toResponse);
    }

    @Override
    public Optional<AdministradorResponse> buscarAdministradorPorId(Long id) {
        return administradorRepository.findByIdAndAtivoTrue(id)
                .map(administradorMapper::toResponse);
    }

    @Override
    public AdministradorResponse cadastrarAdministrador(AdministradorRequest administradorRequest) {
        Administrador admin = administradorMapper.toEntity(administradorRequest);
        // Criptografa a senha
        admin.setSenha(passwordEncoder.encode(admin.getSenha()));
        admin.setAtivo(true);
        administradorRepository.save(admin);
        return administradorMapper.toResponse(admin);
    }

    @Override
    public AdministradorResponse atualizarAdministrador(Long id, AdministradorRequest administradorRequest) {
        Administrador admin = administradorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrador não encontrado"));

        // Atualize os campos (você pode usar o mapper para atualizar também se desejar)
        admin.setNome(administradorRequest.nome());
        admin.setEmail(administradorRequest.email());
        admin.setSetor(administradorRequest.setor());
        // Se desejar atualizar a senha, adicione lógica extra aqui

        administradorRepository.save(admin);
        return administradorMapper.toResponse(admin);
    }

    @Override
    public void desativarAdministrador(Long id) {
        Administrador admin = administradorRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Administrador não encontrado"));
        admin.setAtivo(false);
        administradorRepository.save(admin);
    }

    @Override
    public Optional<Administrador> findByEmail(String email) {
        return administradorRepository.findByEmailAndAtivoTrue(email);
    }
}
