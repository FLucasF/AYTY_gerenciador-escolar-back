package br.com.ufpb.GerenciadorEscolar.util;


import br.com.ufpb.GerenciadorEscolar.model.entity.UserLogin;
import br.com.ufpb.GerenciadorEscolar.model.entity.Usuario;
import br.com.ufpb.GerenciadorEscolar.repository.UsuarioRepository;
import br.com.ufpb.GerenciadorEscolar.service.CpfJaCadastradoException;
import br.com.ufpb.GerenciadorEscolar.service.EmailJaCadastradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component  // 🚀 Agora é um Bean do Spring
public class UsuarioUtils {

    private static final Pattern CPF_PATTERN = Pattern.compile("\\d{11}");
    private static final Pattern SENHA_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioUtils(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Valida o formato do CPF (apenas números e 11 dígitos).
     */
    public boolean isCpfValido(String cpf) {
        return cpf != null && CPF_PATTERN.matcher(cpf).matches();
    }

    /**
     * Valida se a senha atende aos critérios de segurança.
     */
    public boolean isSenhaValida(String senha) {
        return senha != null && SENHA_PATTERN.matcher(senha).matches();
    }

    /**
     * Atualiza os dados de um usuário e seu UserLogin sem sobrescrever a senha caso não tenha sido alterada.
     *
     * @return `true` se houver alteração nos dados do usuário ou do UserLogin.
     */
    public boolean atualizarDadosUsuario(Usuario usuario, UserLogin userLogin,
                                         String novoNome, String novoEmail,
                                         String novoCpf, String novaSenha,
                                         PasswordEncoder passwordEncoder) {
        boolean alterado = false;

        // ✅ Atualiza Nome se for diferente
        if (novoNome != null && !novoNome.equals(usuario.getNome())) {
            usuario.setNome(novoNome);
            alterado = true;
        }

        // ✅ Atualiza Email se for diferente e NÃO estiver cadastrado
        if (novoEmail != null && !novoEmail.equals(usuario.getEmail())) {
            boolean emailExiste = usuarioRepository.findByEmailAndAtivoTrue(novoEmail)
                    .filter(u -> !u.getId().equals(usuario.getId())) // Garante que não está verificando o próprio email
                    .isPresent();

            if (emailExiste) {
                throw new EmailJaCadastradoException("Já existe outro usuário ativo com este e-mail.");
            }
            usuario.setEmail(novoEmail);
            userLogin.setEmail(novoEmail);
            alterado = true;
        }

        // ✅ Atualiza CPF se for diferente e válido
        if (novoCpf != null && !novoCpf.equals(usuario.getCpf())) {
            if (!isCpfValido(novoCpf)) {
                throw new IllegalArgumentException("CPF inválido! Deve conter 11 dígitos numéricos.");
            }
            boolean cpfExiste = usuarioRepository.findByCpfAndAtivoTrue(novoCpf)
                    .filter(u -> !u.getId().equals(usuario.getId()))
                    .isPresent();
            if (cpfExiste) {
                throw new CpfJaCadastradoException("Já existe outro usuário ativo com este CPF.");
            }
            usuario.setCpf(novoCpf);
            alterado = true;
        }

        // ✅ Atualiza Senha se for diferente da atual
        if (novaSenha != null && !novaSenha.trim().isEmpty()) {
            boolean senhaIgual = passwordEncoder.matches(novaSenha, usuario.getSenha());

            if (!senhaIgual) {  // Apenas altera se for diferente
                if (!isSenhaValida(novaSenha)) {
                    throw new IllegalArgumentException("A senha deve ter pelo menos 8 caracteres, incluindo uma letra maiúscula, uma minúscula, um número e um caractere especial.");
                }
                String senhaCriptografada = passwordEncoder.encode(novaSenha);
                usuario.setSenha(senhaCriptografada);
                userLogin.setSenha(senhaCriptografada);
                alterado = true;
            }
        }

        return alterado;
    }

}