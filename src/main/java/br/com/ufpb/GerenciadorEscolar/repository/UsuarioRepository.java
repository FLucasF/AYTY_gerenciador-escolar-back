package br.com.ufpb.GerenciadorEscolar.repository;

import br.com.ufpb.GerenciadorEscolar.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // 🔹 Busca todos os usuários ativos com paginação
    Page<Usuario> findByAtivoTrue(Pageable pageable);

    // 🔹 Busca um usuário ativo pelo e-mail (para verificar duplicação no cadastro)
    Optional<Usuario> findByEmailAndAtivoTrue(String email);

    // 🔹 Busca um usuário ativo pelo CPF (para evitar CPFs duplicados)
    Optional<Usuario> findByCpfAndAtivoTrue(String cpf);

}
