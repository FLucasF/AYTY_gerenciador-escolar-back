package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.dto.mural.MuralRequest;
import br.com.ufpb.GerenciadorEscolar.dto.mural.MuralResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MuralServiceInterface {

    MuralResponse criarPostagem(MuralRequest muralRequest);

    MuralResponse buscarPostagemPorId(Long id);

    Page<MuralResponse> listarPostagensPorTurma(Long idTurma, Pageable pageable);

    void deletarPostagem(Long id);
}
