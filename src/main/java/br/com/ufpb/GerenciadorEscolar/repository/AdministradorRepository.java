package br.com.ufpb.GerenciadorEscolar.repository;

import br.com.ufpb.GerenciadorEscolar.model.Administrador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Long> {

    // Busca por email apenas entre os administradores ativos
    Optional<Administrador> findByEmailAndAtivoTrue(String email);

    // Retorna todos os administradores ativos com paginação
    Page<Administrador> findAllByAtivoTrue(Pageable pageable);

    // Retorna o administrador ativo com o id informado
    Optional<Administrador> findByIdAndAtivoTrue(Long id);
}
