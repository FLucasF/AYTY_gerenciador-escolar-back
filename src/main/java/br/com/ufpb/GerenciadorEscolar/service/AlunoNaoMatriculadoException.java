package br.com.ufpb.GerenciadorEscolar.service;

public class AlunoNaoMatriculadoException extends RuntimeException {
    public AlunoNaoMatriculadoException(String mensagem) {
        super(mensagem);
    }
}
