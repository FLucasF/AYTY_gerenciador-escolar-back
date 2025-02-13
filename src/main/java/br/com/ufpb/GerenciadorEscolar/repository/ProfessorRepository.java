package br.com.ufpb.GerenciadorEscolar.repository;

import br.com.ufpb.GerenciadorEscolar.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {

    Optional<Professor> findByEmail(String email);

    // ✅ Spring gera automaticamente a consulta para buscar professores ativos
    List<Professor> findAllByAtivoTrue();

    // ✅ Spring gera automaticamente a consulta para buscar um professor ativo pelo ID
    Optional<Professor> findByIdAndAtivoTrue(Long id);

    // ✅ Spring gera automaticamente a consulta para buscar um professor ativo pelo email
    Optional<Professor> findByEmailAndAtivoTrue(String email);
}

