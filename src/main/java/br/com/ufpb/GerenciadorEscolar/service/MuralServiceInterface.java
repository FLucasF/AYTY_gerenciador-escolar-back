package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.model.Mural;
import java.util.List;

public interface MuralServiceInterface {
    Mural criarPostagem(Mural mural);
    List<Mural> listarPostagensPorTurma(Long idTurma);
    void deletarPostagem(Long id);
}
