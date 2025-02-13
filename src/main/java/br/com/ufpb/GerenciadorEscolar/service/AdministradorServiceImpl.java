package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.model.Administrador;
import br.com.ufpb.GerenciadorEscolar.repository.AdministradorRepository;
import br.com.ufpb.GerenciadorEscolar.service.interfaces.AdministradorServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdministradorServiceImpl implements AdministradorServiceInterface {

    @Autowired
    private AdministradorRepository administradorRepository;

    @Override
    public List<Administrador> listarAdministradoresAtivos() {
        return administradorRepository.findAllByAtivoTrue();
    }

    @Override
    public Optional<Administrador> buscarAdministradorPorId(Long id) {
        return administradorRepository.findByIdAndAtivoTrue(id);
    }

    @Override
    public Administrador cadastrarAdministrador(Administrador administrador) {
        return administradorRepository.save(administrador);
    }

    @Override
    public Administrador atualizarAdministrador(Long id, Administrador administrador) {
        Administrador adminExistente = administradorRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Administrador não encontrado"));

        adminExistente.setNome(administrador.getNome());
        adminExistente.setEmail(administrador.getEmail());
        adminExistente.setSetor(administrador.getSetor());

        return administradorRepository.save(adminExistente);
    }

    @Override
    public void desativarAdministrador(Long id) {
        Administrador admin = administradorRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Administrador não encontrado"));

        admin.setAtivo(false);
        administradorRepository.save(admin);
    }
}
