package br.com.ufpb.GerenciadorEscolar.repository;

import br.com.ufpb.GerenciadorEscolar.model.Mural;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MuralRepository extends JpaRepository<Mural, Long> {
    List<Mural> findByTurmaId(Long turmaId);
}
