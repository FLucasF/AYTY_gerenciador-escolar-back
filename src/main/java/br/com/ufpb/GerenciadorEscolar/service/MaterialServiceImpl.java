package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.dto.material.MaterialRequest;
import br.com.ufpb.GerenciadorEscolar.dto.material.MaterialResponse;
import br.com.ufpb.GerenciadorEscolar.mapper.MaterialMapper;
import br.com.ufpb.GerenciadorEscolar.model.Material;
import br.com.ufpb.GerenciadorEscolar.repository.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MaterialServiceImpl implements MaterialServiceInterface {

    private final MaterialRepository materialRepository;
    private final MaterialMapper materialMapper;

    @Autowired
    public MaterialServiceImpl(MaterialRepository materialRepository, MaterialMapper materialMapper) {
        this.materialRepository = materialRepository;
        this.materialMapper = materialMapper;
    }

    @Override
    public MaterialResponse enviarMaterial(MaterialRequest materialRequest) {
        Material material = materialMapper.toEntity(materialRequest);
        materialRepository.save(material);
        return materialMapper.toResponse(material);
    }

    @Override
    public Optional<MaterialResponse> buscarMaterialPorId(Long id) {
        return materialRepository.findById(id).map(materialMapper::toResponse);
    }

    @Override
    public List<MaterialResponse> listarMateriaisPorTurma(Long idTurma) {
        return materialRepository.findByTurmaIdAndAtivoTrue(idTurma).stream().map(materialMapper::toResponse).toList();
    }

    @Override
    public void deletarMaterial(Long id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material não encontrado"));
        material.setAtivo(false);
        materialRepository.save(material);
    }
}
