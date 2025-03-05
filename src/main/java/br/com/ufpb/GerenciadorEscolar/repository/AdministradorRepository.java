package br.com.ufpb.GerenciadorEscolar.repository;

import br.com.ufpb.GerenciadorEscolar.model.Administrador;
import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Long> {
    Optional<Administrador> findByEmailAndAtivoTrue(String email);

    Page<Administrador> findAllByAtivoTrue(Pageable pageable);

    Optional<Administrador> findByIdAndAtivoTrue(Long id);

    Optional<Administrador> findByCpfAndAtivoTrue(String cpf);

}
