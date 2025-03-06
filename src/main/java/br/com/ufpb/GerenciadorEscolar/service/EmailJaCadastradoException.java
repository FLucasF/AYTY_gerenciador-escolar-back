package br.com.ufpb.GerenciadorEscolar.service;

public class EmailJaCadastradoException extends RuntimeException {
    public EmailJaCadastradoException(String mensagem) {
        super(mensagem);
    }
}
