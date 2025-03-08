package br.com.ufpb.GerenciadorEscolar.repository;

import br.com.ufpb.GerenciadorEscolar.model.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {

    Page<Material> findByTurmaIdAndAtivoTrue(Long turmaId, Pageable pageable);

    /**
     * Busca todos os materiais pelo ID e verifica se estão ativos.
     *
     * @param ids Lista de IDs dos materiais
     * @return Lista de materiais ativos encontrados
     */
    List<Material> findAllByIdInAndAtivoTrue(List<Long> ids);

}
