package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.dto.material.MaterialRequest;
import br.com.ufpb.GerenciadorEscolar.dto.material.MaterialResponse;
import java.util.List;
import java.util.Optional;

public interface MaterialServiceInterface {

    MaterialResponse enviarMaterial(MaterialRequest materialRequest);

    Optional<MaterialResponse> buscarMaterialPorId(Long id);

    List<MaterialResponse> listarMateriaisPorTurma(Long idTurma);

    void deletarMaterial(Long id);
}
