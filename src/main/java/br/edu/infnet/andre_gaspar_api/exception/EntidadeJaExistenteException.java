package br.edu.infnet.andre_gaspar_api.exception;

public class EntidadeJaExistenteException extends RuntimeException {

    public EntidadeJaExistenteException(String mensagem) {
        super(mensagem);
    }
}