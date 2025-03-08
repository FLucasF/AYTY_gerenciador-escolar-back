package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.dto.material.MaterialRequest;
import br.com.ufpb.GerenciadorEscolar.dto.material.MaterialResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import br.com.ufpb.GerenciadorEscolar.model.Mural;

public interface MaterialServiceInterface {

    /**
     * Salva um material no MinIO e no banco de dados.
     *
     * @param materialRequest Dados do material
     * @param file Arquivo em formato de bytes para upload
     * @return MaterialResponse contendo os dados salvos e a URL do arquivo
     */
    MaterialResponse salvarMaterial(MaterialRequest materialRequest, byte[] file);

    /**
     * Busca um material pelo ID e retorna os detalhes, incluindo a URL assinada do arquivo.
     *
     * @param id ID do material
     * @return MaterialResponse contendo os detalhes do material
     */
    MaterialResponse buscarMaterialPorId(Long id);

    /**
     * Atualiza um material existente no MinIO e no banco de dados.
     *
     * @param id ID do material a ser atualizado
     * @param materialRequest Dados atualizados do material
     * @param file Novo arquivo em formato de bytes para substituição
     * @return MaterialResponse com os novos detalhes do material atualizado
     */
    MaterialResponse atualizarMaterial(Long id, MaterialRequest materialRequest, byte[] file);

    /**
     * Desativa um material no banco de dados e remove a mídia do MinIO.
     *
     * @param id ID do material
     */
    void deletarMaterial(Long id);

    /**
     * Lista os materiais associados a uma determinada entidade (por exemplo, Turma) com paginação.
     *
     * @param serviceName Nome do serviço
     * @param turmaId ID da turma
     * @param pageable Objeto de paginação
     * @return Página de MaterialResponse contendo os materiais encontrados
     */
    Page<MaterialResponse> listarMateriaisPorEntidade(String serviceName, Long turmaId, Pageable pageable);

    /**
     * Associa um material a um mural, atualizando o campo mural da entidade Material.
     *
     * @param materialId ID do material a ser associado
     * @param mural Objeto Mural ao qual o material será vinculado
     */
    void associarMaterialAoMural(Long materialId, Mural mural);
}
