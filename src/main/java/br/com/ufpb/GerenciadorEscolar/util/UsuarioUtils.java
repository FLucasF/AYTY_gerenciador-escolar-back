package br.com.ufpb.GerenciadorEscolar.util;

import br.com.ufpb.GerenciadorEscolar.model.Usuario;
import br.com.ufpb.GerenciadorEscolar.model.UserLogin;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.regex.Pattern;

public class UsuarioUtils {

    private static final Pattern CPF_PATTERN = Pattern.compile("\\d{11}");
    private static final Pattern SENHA_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

    /**
     * Valida o formato do CPF (apenas números e 11 dígitos).
     */
    public static boolean isCpfValido(String cpf) {
        return cpf != null && CPF_PATTERN.matcher(cpf).matches();
    }

    /**
     * Valida se a senha atende aos critérios de segurança.
     */
    public static boolean isSenhaValida(String senha) {
        return senha != null && SENHA_PATTERN.matcher(senha).matches();
    }

    /**
     * Atualiza os dados de um usuário e seu UserLogin sem sobrescrever a senha caso não tenha sido alterada.
     *
     * @return `true` se houver alteração nos dados do usuário ou do UserLogin.
     */
    public static boolean atualizarDadosUsuario(Usuario usuario, UserLogin userLogin,
                                                String novoNome, String novoEmail,
                                                String novoCpf, String novaSenha,
                                                PasswordEncoder passwordEncoder) {
        boolean alterado = false;

        if (novoNome != null && !novoNome.equals(usuario.getNome())) {
            usuario.setNome(novoNome);
            alterado = true;
        }

        if (novoEmail != null && !novoEmail.equals(usuario.getEmail())) {
            usuario.setEmail(novoEmail);
            userLogin.setEmail(novoEmail);
            alterado = true;
        }

        if (novoCpf != null && !novoCpf.equals(usuario.getCpf()) && isCpfValido(novoCpf)) {
            usuario.setCpf(novoCpf);
            alterado = true;
        }

        if (novaSenha != null && !novaSenha.trim().isEmpty() && !passwordEncoder.matches(novaSenha, usuario.getSenha())) {
            if (!isSenhaValida(novaSenha)) {
                throw new IllegalArgumentException("A senha deve ter pelo menos 8 caracteres, incluindo uma letra maiúscula, uma minúscula, um número e um caractere especial.");
            }
            usuario.setSenha(passwordEncoder.encode(novaSenha));
            userLogin.setSenha(passwordEncoder.encode(novaSenha));
            alterado = true;
        }

        return alterado;
    }
}
