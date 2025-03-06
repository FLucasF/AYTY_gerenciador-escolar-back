package br.com.ufpb.GerenciadorEscolar.service;

public class AdministradorNaoEncontradoException extends RuntimeException {
    public AdministradorNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
