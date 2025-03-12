package br.com.ufpb.GerenciadorEscolar.mapper;

import br.com.ufpb.GerenciadorEscolar.model.dto.mural.MuralRequest;
import br.com.ufpb.GerenciadorEscolar.model.dto.mural.MuralResponse;
import br.com.ufpb.GerenciadorEscolar.model.entity.Mural;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MuralMapper {
    @Mapping(target = "professorId", source = "professor.id")
    MuralResponse toResponse(Mural mural);
    Mural toEntity(MuralRequest muralRequest);
}
