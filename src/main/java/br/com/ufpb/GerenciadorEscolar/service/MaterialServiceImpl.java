package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.model.Material;
import br.com.ufpb.GerenciadorEscolar.repository.MaterialRepository;
import br.com.ufpb.GerenciadorEscolar.service.interfaces.MaterialServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaterialServiceImpl implements MaterialServiceInterface {

    @Autowired
    private MaterialRepository materialRepository;

    @Override
    public Material enviarMaterial(Material material) {
        return materialRepository.save(material);
    }

    @Override
    public List<Material> listarMateriaisPorTurma(Long idTurma) {
        return materialRepository.findByTurmaId(idTurma);
    }

    @Override
    public void deletarMaterial(Long id) {
        materialRepository.deleteById(id);
    }

    public Material buscarMaterialPorId(Long id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material não encontrado"));
    }

}
