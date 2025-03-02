package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.dto.material.MaterialRequest;
import br.com.ufpb.GerenciadorEscolar.dto.material.MaterialResponse;
import br.com.ufpb.GerenciadorEscolar.mapper.MaterialMapper;
import br.com.ufpb.GerenciadorEscolar.model.Material;
import br.com.ufpb.GerenciadorEscolar.repository.MaterialRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
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
        log.info("Enviando material com os dados: {}", materialRequest);
        Material material = materialMapper.toEntity(materialRequest);
        try {
            materialRepository.save(material);
            log.info("Material salvo com sucesso. ID: {}", material.getId());
        } catch (Exception e) {
            log.error("Erro ao salvar material: {}", e.getMessage());
            throw new RuntimeException("Erro ao salvar material: " + e.getMessage(), e);
        }
        MaterialResponse response = materialMapper.toResponse(material);
        log.info("Retornando resposta do material: {}", response);
        return response;
    }

    @Override
    public Optional<MaterialResponse> buscarMaterialPorId(Long id) {
        log.info("Buscando material por ID: {}", id);
        Optional<MaterialResponse> response = materialRepository.findById(id)
                .map(materialMapper::toResponse);
        if (response.isEmpty()) {
            log.warn("Material não encontrado para o ID: {}", id);
        } else {
            log.debug("Material encontrado: {}", response.get());
        }
        return response;
    }

    @Override
    public List<MaterialResponse> listarMateriaisPorTurma(Long idTurma) {
        log.info("Listando materiais para a turma com ID: {}", idTurma);
        List<MaterialResponse> responses = materialRepository.findByTurmaIdAndAtivoTrue(idTurma)
                .stream()
                .map(materialMapper::toResponse)
                .toList();
        log.info("Total de materiais encontrados para a turma {}: {}", idTurma, responses.size());
        return responses;
    }

    @Override
    public void deletarMaterial(Long id) {
        log.info("Deletando material com ID: {}", id);
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Material não encontrado para deleção com ID: {}", id);
                    return new RuntimeException("Material não encontrado");
                });
        material.setAtivo(false);
        materialRepository.save(material);
        log.info("Material desativado com sucesso. ID: {}", id);
    }
}
