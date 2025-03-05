package br.com.ufpb.GerenciadorEscolar.service;

public class NenhumaAlteracaoRealizadaException extends RuntimeException {
    public NenhumaAlteracaoRealizadaException() {
        super("Os dados enviados são idênticos aos já cadastrados.");
    }
}
