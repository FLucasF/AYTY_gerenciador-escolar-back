package br.com.ufpb.GerenciadorEscolar.service;

public class MaterialNaoEncontradoException extends RuntimeException {
    public MaterialNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}


