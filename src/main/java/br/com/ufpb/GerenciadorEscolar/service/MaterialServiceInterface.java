package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.model.Material;
import java.util.List;

public interface MaterialServiceInterface {
    Material enviarMaterial(Material material);
    List<Material> listarMateriaisPorTurma(Long idTurma);
    void deletarMaterial(Long id);
}
