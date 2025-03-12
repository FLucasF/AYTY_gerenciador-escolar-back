package br.com.ufpb.GerenciadorEscolar.service;

public class LoginNaoEncontradoException extends RuntimeException {
    public LoginNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}