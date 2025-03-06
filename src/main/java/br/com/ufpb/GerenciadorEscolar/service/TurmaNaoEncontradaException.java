package br.com.ufpb.GerenciadorEscolar.service;

public class TurmaNaoEncontradaException extends RuntimeException {
    public TurmaNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}
