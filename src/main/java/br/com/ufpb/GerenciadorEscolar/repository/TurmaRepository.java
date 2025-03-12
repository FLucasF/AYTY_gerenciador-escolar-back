package br.com.ufpb.GerenciadorEscolar.repository;

import br.com.ufpb.GerenciadorEscolar.model.entity.Turma;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TurmaRepository extends JpaRepository<Turma, Long> {

    Page<Turma> findByProfessorIdAndAtivoTrue(Long professorId, Pageable pageable);

    Page<Turma> findByAlunosIdAndAtivoTrue(Long alunoId, Pageable pageable);

    Optional<Turma> findByIdAndAtivoTrue(Long id);

}
