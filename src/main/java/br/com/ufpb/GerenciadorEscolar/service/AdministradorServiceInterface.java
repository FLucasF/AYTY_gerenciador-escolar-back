package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorRequest;
import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorResponse;
import br.com.ufpb.GerenciadorEscolar.model.Administrador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AdministradorServiceInterface {
    Page<AdministradorResponse> listarAdministradoresAtivos(Pageable pageable);
    Optional<AdministradorResponse> buscarAdministradorPorId(Long id);
    void desativarAdministrador(Long id);
    AdministradorResponse cadastrarAdministrador(AdministradorRequest administradorRequest);
    AdministradorResponse atualizarAdministrador(Long id, AdministradorRequest administradorRequest);
    Optional<Administrador> findByEmail(String email);
}
