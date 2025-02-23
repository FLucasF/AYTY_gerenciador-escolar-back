package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.dto.mural.MuralRequest;
import br.com.ufpb.GerenciadorEscolar.dto.mural.MuralResponse;
import java.util.List;
import java.util.Optional;

public interface MuralServiceInterface {

    MuralResponse criarPostagem(MuralRequest muralRequest);

    Optional<MuralResponse> buscarPostagemPorId(Long id);

    List<MuralResponse> listarPostagensPorTurma(Long idTurma);

    void deletarPostagem(Long id);
}
