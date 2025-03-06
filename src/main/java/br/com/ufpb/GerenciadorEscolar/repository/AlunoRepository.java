package br.com.ufpb.GerenciadorEscolar.repository;

import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    Optional<Aluno> findByEmailAndAtivoTrue(String email);

    Page<Aluno> findAllByAtivoTrue(Pageable pageable);

    Optional<Aluno> findByIdAndAtivoTrue(Long id);

    Optional<Aluno> findByCpfAndAtivoTrue(String cpf);

    Page<Aluno> findByTurmasId(Long turmaId, Pageable pageable);


}
