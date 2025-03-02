package br.com.ufpb.GerenciadorEscolar.repository;

import br.com.ufpb.GerenciadorEscolar.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserLoginRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailAndAtivoTrue(String email);

}
