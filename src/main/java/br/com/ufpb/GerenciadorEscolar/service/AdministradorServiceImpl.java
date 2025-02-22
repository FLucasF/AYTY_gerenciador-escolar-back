package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorRequest;
import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorResponse;
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

    @Autowired
    public AdministradorServiceImpl(AdministradorRepository administradorRepository, PasswordEncoder passwordEncoder) {
        this.administradorRepository = administradorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Page<AdministradorResponse> listarAdministradoresAtivos(Pageable pageable) {
        return administradorRepository.findAllByAtivoTrue(pageable)
                .map(admin -> new AdministradorResponse(admin.getId(), admin.getNome(), admin.getEmail(), admin.getCpf(), admin.getSetor(), admin.getSiape()));
    }

    @Override
    public Optional<AdministradorResponse> buscarAdministradorPorId(Long id) {
        return administradorRepository.findByIdAndAtivoTrue(id)
                .map(admin -> new AdministradorResponse(admin.getId(), admin.getNome(), admin.getEmail(), admin.getCpf(), admin.getSetor(), admin.getSiape()));
    }

    @Override
    public AdministradorResponse cadastrarAdministrador(AdministradorRequest administradorRequest) {
        Administrador admin = new Administrador(
                administradorRequest.nome(),
                administradorRequest.email(),
                passwordEncoder.encode(administradorRequest.senha()),
                administradorRequest.cpf(),
                administradorRequest.setor(),
                administradorRequest.siape()
        );
        admin.setAtivo(true);
        administradorRepository.save(admin);

        return new AdministradorResponse(admin.getId(), admin.getNome(), admin.getEmail(), admin.getCpf(), admin.getSetor(), admin.getSiape());
    }

    @Override
    public AdministradorResponse atualizarAdministrador(Long id, AdministradorRequest administradorRequest) {
        Administrador admin = administradorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrador não encontrado"));

        admin.setNome(administradorRequest.nome());
        admin.setEmail(administradorRequest.email());
        admin.setSetor(administradorRequest.setor());

        administradorRepository.save(admin);
        return new AdministradorResponse(admin.getId(), admin.getNome(), admin.getEmail(), admin.getCpf(), admin.getSetor(), admin.getSiape());
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
