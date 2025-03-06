package br.com.ufpb.GerenciadorEscolar.service;

public class PostagemNaoEncontradaException extends RuntimeException {
    public PostagemNaoEncontradaException(String message) {
        super(message);
    }
}
