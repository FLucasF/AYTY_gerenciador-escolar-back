package br.com.ufpb.GerenciadorEscolar.repository;

import br.com.ufpb.GerenciadorEscolar.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.util.Optional;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Long> {

    Optional<Administrador> findByEmail(String email);

    List<Administrador> findAllByAtivoTrue();

    Optional<Administrador> findByIdAndAtivoTrue(Long id);

    Optional<Administrador> findByEmailAndAtivoTrue(String email);
}

