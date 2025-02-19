package br.com.ufpb.GerenciadorEscolar.service.interfaces;

import br.com.ufpb.GerenciadorEscolar.dto.AdministradorDTO;
import br.com.ufpb.GerenciadorEscolar.model.Administrador;

import java.util.List;
import java.util.Optional;

public interface AdministradorServiceInterface {
    List<Administrador> listarAdministradoresAtivos();
    Optional<Administrador> buscarAdministradorPorId(Long id);
    void desativarAdministrador(Long id);
    Administrador cadastrarAdministrador(Administrador administrador);
    Administrador atualizarAdministrador(Long id, Administrador administrador);
    Optional<Administrador> findByEmail(String email);

}
