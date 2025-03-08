package br.com.ufpb.GerenciadorEscolar.mapper;

import br.com.ufpb.GerenciadorEscolar.dto.material.MaterialRequest;
import br.com.ufpb.GerenciadorEscolar.dto.material.MaterialResponse;
import br.com.ufpb.GerenciadorEscolar.model.Material;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface MaterialMapper {

    @Mapping(source = "turma.id", target = "turmaId")
    @Mapping(source = "professor.id", target = "professorId")
    @Mapping(source = "arquivoId", target = "urlArquivo", qualifiedByName = "mapToSignedUrl")
    MaterialResponse toResponse(Material material);

    @Mapping(target = "id", ignore = true) // ID gerado pelo banco
    @Mapping(target = "turma", ignore = true) // Turma será atribuída no service
    @Mapping(target = "professor", ignore = true) // Professor será atribuído no service
    @Mapping(target = "ativo", constant = "true") // Sempre começa como ativo
    Material toEntity(MaterialRequest materialRequest);

    @Named("mapToSignedUrl")
    static String mapToSignedUrl(String arquivoId) {
        if (arquivoId == null) return null;
        return "/api/media/get/" + Material.SERVICE_NAME + "/" + arquivoId;
    }
}
