package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorRequest;
import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorResponse;
import br.com.ufpb.GerenciadorEscolar.model.Administrador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AdministradorServiceInterface {

    /**
     * Lista todos os administradores ativos com paginação.
     */
    Page<AdministradorResponse> listarAdministradoresAtivos(Pageable pageable);

    /**
     * Busca um administrador ativo pelo ID.
     * @throws AdministradorNaoEncontradoException se o administrador não for encontrado.
     */
    AdministradorResponse buscarAdministradorPorId(Long id);

    /**
     * Cadastra um novo administrador no sistema.
     * @throws EmailJaCadastradoException se o e-mail já estiver cadastrado.
     * @throws CpfJaCadastradoException se o CPF já estiver cadastrado.
     */
    AdministradorResponse cadastrarAdministrador(AdministradorRequest administradorRequest);

    /**
     * Atualiza um administrador ativo pelo ID.
     * @throws AdministradorNaoEncontradoException se o administrador não for encontrado.
     * @throws NenhumaAlteracaoRealizadaException se nenhuma alteração foi feita nos dados.
     */
    AdministradorResponse atualizarAdministrador(Long id, AdministradorRequest administradorRequest);

    /**
     * Desativa um administrador pelo ID.
     * @throws AdministradorNaoEncontradoException se o administrador não for encontrado.
     */
    void desativarAdministrador(Long id);

}
