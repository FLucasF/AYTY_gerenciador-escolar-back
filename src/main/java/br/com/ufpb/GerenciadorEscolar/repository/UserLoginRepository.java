package br.com.ufpb.GerenciadorEscolar.repository;

import br.com.ufpb.GerenciadorEscolar.model.UserLogin;
import br.com.ufpb.GerenciadorEscolar.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserLoginRepository extends JpaRepository<UserLogin, Long> {

    Optional<UserLogin> findByEmailAndAtivoTrue(String email);

    Optional<UserLogin> findByUsuarioAndAtivoTrue(Usuario usuario);

}

