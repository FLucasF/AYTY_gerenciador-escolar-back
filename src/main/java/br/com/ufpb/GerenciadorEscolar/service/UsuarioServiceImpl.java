package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.dto.usuario.UsuarioResponse;
import br.com.ufpb.GerenciadorEscolar.mapper.UsuarioMapper;
import br.com.ufpb.GerenciadorEscolar.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements UsuarioServiceInterface {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

    @Override
    public Page<UsuarioResponse> listarUsuarios(Pageable pageable) {
        return usuarioRepository.findByAtivoTrue(pageable)
                .map(usuarioMapper::toResponse);
    }
}
