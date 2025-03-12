package br.com.ufpb.GerenciadorEscolar.mapper;

import br.com.ufpb.GerenciadorEscolar.model.dto.administrador.AdministradorRequest;
import br.com.ufpb.GerenciadorEscolar.model.dto.administrador.AdministradorResponse;
import br.com.ufpb.GerenciadorEscolar.model.entity.Administrador;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdministradorMapper {
    AdministradorResponse toResponse(Administrador admin);
    Administrador toEntity(AdministradorRequest administradorRequest);
}
