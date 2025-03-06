package br.com.ufpb.GerenciadorEscolar.service;

public class SiapeJaCadastradoException extends RuntimeException {
    public SiapeJaCadastradoException(String mensagem) {
        super(mensagem);
    }
}
