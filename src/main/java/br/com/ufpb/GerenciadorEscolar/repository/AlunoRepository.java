package br.com.ufpb.GerenciadorEscolar.repository;

import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    // Busca por email apenas entre os alunos ativos
    Optional<Aluno> findByEmailAndAtivoTrue(String email);

    // Retorna todos os alunos ativos com paginação
    Page<Aluno> findAllByAtivoTrue(Pageable pageable);

    // Retorna o aluno ativo com o id informado
    Optional<Aluno> findByIdAndAtivoTrue(Long id);

}
