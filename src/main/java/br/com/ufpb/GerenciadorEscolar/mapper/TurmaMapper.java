package br.com.ufpb.GerenciadorEscolar.mapper;

import br.com.ufpb.GerenciadorEscolar.dto.turma.TurmaRequest;
import br.com.ufpb.GerenciadorEscolar.dto.turma.TurmaResponse;
import br.com.ufpb.GerenciadorEscolar.model.Turma;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TurmaMapper {
    TurmaResponse toResponse(Turma turma);
    Turma toEntity(TurmaRequest turmaRequest);
}
