package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.model.dto.usuario.UsuarioResponse;
import br.com.ufpb.GerenciadorEscolar.mapper.UsuarioMapper;
import br.com.ufpb.GerenciadorEscolar.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UsuarioServiceImpl implements UsuarioServiceInterface {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

    /**
     * Listar usuários ativos com paginação.
     *
     * Este método recupera uma lista paginada de usuários ativos no sistema.
     *
     * @param pageable - Objeto `Pageable` contendo as informações de paginação (página, tamanho, ordenação).
     * @return Page<UsuarioResponse> - Retorna uma página contendo os usuários ativos no formato `UsuarioResponse`.
     */
    @Override
    public Page<UsuarioResponse> listarUsuarios(Pageable pageable) {
        log.info("Iniciando listagem de usuários ativos com paginação: {}", pageable);

        // Recupera os usuários ativos do banco de dados e converte para DTOs `UsuarioResponse`
        Page<UsuarioResponse> usuariosPage = usuarioRepository.findByAtivoTrue(pageable)
                .map(usuarioMapper::toResponse);

        log.info("Listagem concluída. Total de usuários ativos encontrados: {}", usuariosPage.getTotalElements());

        return usuariosPage;
    }
}