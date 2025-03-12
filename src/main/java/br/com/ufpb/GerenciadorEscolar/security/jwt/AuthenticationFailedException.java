package br.com.ufpb.GerenciadorEscolar.security.jwt;

public class AuthenticationFailedException extends RuntimeException {
    public AuthenticationFailedException(String mensagem) {
        super(mensagem);
    }
}