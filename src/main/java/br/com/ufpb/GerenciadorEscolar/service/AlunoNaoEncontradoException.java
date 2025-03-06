package br.com.ufpb.GerenciadorEscolar.service;

public class AlunoNaoEncontradoException extends RuntimeException {
    public AlunoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}