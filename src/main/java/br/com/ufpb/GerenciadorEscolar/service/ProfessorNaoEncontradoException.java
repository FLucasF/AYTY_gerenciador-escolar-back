package br.com.ufpb.GerenciadorEscolar.service;

public class ProfessorNaoEncontradoException extends RuntimeException {
    public ProfessorNaoEncontradoException(String message) {
        super(message);
    }
}