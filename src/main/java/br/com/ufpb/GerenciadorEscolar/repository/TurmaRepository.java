package br.com.ufpb.GerenciadorEscolar.repository;

import br.com.ufpb.GerenciadorEscolar.model.Turma;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface TurmaRepository extends JpaRepository<Turma, Long> {

    Page<Turma> findByProfessorIdAndAtivoTrue(Long professorId, Pageable pageable);

    Page<Turma> findByAlunosIdAndAtivoTrue(Long alunoId, Pageable pageable);

    /**
     * Busca uma turma pelo ID e verifica se ela está ativa.
     *
     * @param id ID da turma
     * @return Optional contendo a turma ativa, se encontrada
     */
    Optional<Turma> findByIdAndAtivoTrue(Long id);

}
