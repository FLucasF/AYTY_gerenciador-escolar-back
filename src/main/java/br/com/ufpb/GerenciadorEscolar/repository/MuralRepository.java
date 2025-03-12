package br.com.ufpb.GerenciadorEscolar.repository;

import br.com.ufpb.GerenciadorEscolar.model.entity.Mural;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MuralRepository extends JpaRepository<Mural, Long> {

    Page<Mural> findByTurmaIdAndAtivoTrue(Long turmaId, Pageable pageable);
    Optional<Mural> findByIdAndAtivoTrue(Long id);

}
