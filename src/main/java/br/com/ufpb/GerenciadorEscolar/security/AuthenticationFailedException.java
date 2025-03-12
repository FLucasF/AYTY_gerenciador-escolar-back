package br.com.ufpb.GerenciadorEscolar.security;

public class AuthenticationFailedException extends RuntimeException {
    public AuthenticationFailedException(String mensagem) {
        super(mensagem);
    }
}