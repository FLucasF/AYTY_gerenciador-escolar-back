package br.com.ufpb.GerenciadorEscolar.mapper;

import br.com.ufpb.GerenciadorEscolar.dto.material.MaterialRequest;
import br.com.ufpb.GerenciadorEscolar.dto.material.MaterialResponse;
import br.com.ufpb.GerenciadorEscolar.model.Material;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MaterialMapper {

    @Mapping(source = "turma.id", target = "turmaId")
    @Mapping(source = "professor.id", target = "professorId")
    MaterialResponse toResponse(Material material);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "turma", ignore = true)
    @Mapping(target = "professor", ignore = true)
    Material toEntity(MaterialRequest materialRequest);
}
