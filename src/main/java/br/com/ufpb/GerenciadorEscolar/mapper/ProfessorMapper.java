package br.com.ufpb.GerenciadorEscolar.mapper;

import br.com.ufpb.GerenciadorEscolar.model.dto.professor.ProfessorRequest;
import br.com.ufpb.GerenciadorEscolar.model.dto.professor.ProfessorResponse;
import br.com.ufpb.GerenciadorEscolar.model.entity.Professor;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfessorMapper {
    ProfessorResponse toResponse(Professor professor);
    Professor toEntity(ProfessorRequest professorRequest);
}
