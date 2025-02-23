package br.com.ufpb.GerenciadorEscolar.repository;

import br.com.ufpb.GerenciadorEscolar.model.Turma;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TurmaRepository extends JpaRepository<Turma, Long> {

    Page<Turma> findByProfessorId(Long professorId, Pageable pageable);
    Page<Turma> findByAlunosIdAndAlunosAtivoTrue(Long alunoId, Pageable pageable);

}
