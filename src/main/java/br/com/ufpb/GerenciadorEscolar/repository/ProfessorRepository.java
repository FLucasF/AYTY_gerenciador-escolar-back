package br.com.ufpb.GerenciadorEscolar.repository;

import br.com.ufpb.GerenciadorEscolar.model.Professor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {

    // Busca por email apenas entre os professores ativos
    Optional<Professor> findByEmailAndAtivoTrue(String email);

    // Retorna todos os professores ativos com paginação
    Page<Professor> findAllByAtivoTrue(Pageable pageable);

    // Retorna o professor ativo com o id informado
    Optional<Professor> findByIdAndAtivoTrue(Long id);
}
