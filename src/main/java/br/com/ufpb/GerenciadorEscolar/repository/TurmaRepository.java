package br.com.ufpb.GerenciadorEscolar.repository;

import br.com.ufpb.GerenciadorEscolar.model.Turma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TurmaRepository extends JpaRepository<Turma, Long> {

    @Query("SELECT t FROM Turma t WHERE t.professor.id = :professorId")
    List<Turma> findByProfessorId(@Param("professorId") Long professorId);
}
