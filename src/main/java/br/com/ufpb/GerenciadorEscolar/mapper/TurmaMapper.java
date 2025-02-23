package br.com.ufpb.GerenciadorEscolar.mapper;

import br.com.ufpb.GerenciadorEscolar.dto.turma.TurmaRequest;
import br.com.ufpb.GerenciadorEscolar.dto.turma.TurmaResponse;
import br.com.ufpb.GerenciadorEscolar.model.Turma;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TurmaMapper {
    @Mapping(target = "professorId", source = "professor.id")
    TurmaResponse toResponse(Turma turma);

    Turma toEntity(TurmaRequest turmaRequest);
}
