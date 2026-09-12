package br.edu.infnet.andre_gaspar_api.exception;

public class DadosInvalidosException extends RuntimeException {

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}