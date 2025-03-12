package br.com.ufpb.GerenciadorEscolar.mapper;

import br.com.ufpb.GerenciadorEscolar.model.dto.turma.TurmaRequest;
import br.com.ufpb.GerenciadorEscolar.model.dto.turma.TurmaResponse;
import br.com.ufpb.GerenciadorEscolar.model.entity.Turma;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TurmaMapper {
    @Mapping(target = "professorId", source = "professor.id")
    TurmaResponse toResponse(Turma turma);

    Turma toEntity(TurmaRequest turmaRequest);
}
