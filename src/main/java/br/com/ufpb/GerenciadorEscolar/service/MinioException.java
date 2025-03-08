package br.com.ufpb.GerenciadorEscolar.service;

import org.springframework.http.HttpStatus;

public class MinioException extends RuntimeException {
    private final int statusCode;

    public MinioException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public HttpStatus getHttpStatus() {
        return HttpStatus.valueOf(statusCode);
    }
}
