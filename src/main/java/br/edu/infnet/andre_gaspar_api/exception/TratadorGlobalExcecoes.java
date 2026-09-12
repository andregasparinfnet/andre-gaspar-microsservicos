package br.edu.infnet.andre_gaspar_api.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class TratadorGlobalExcecoes {

    @ExceptionHandler(DadosInvalidosException.class)
    public ResponseEntity<ErroApi> tratarDadosInvalidos(
            DadosInvalidosException exception,
            HttpServletRequest request
    ) {
        return criarResposta(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroApi> tratarValidacao(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        String mensagem = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("Dados inválidos");

        return criarResposta(
                HttpStatus.BAD_REQUEST,
                mensagem,
                request
        );
    }

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<ErroApi> tratarEntidadeNaoEncontrada(
            EntidadeNaoEncontradaException exception,
            HttpServletRequest request
    ) {
        return criarResposta(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(EntidadeJaExistenteException.class)
    public ResponseEntity<ErroApi> tratarEntidadeJaExistente(
            EntidadeJaExistenteException exception,
            HttpServletRequest request
    ) {
        return criarResposta(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErroApi> tratarResponseStatusException(
            ResponseStatusException exception,
            HttpServletRequest request
    ) {
        HttpStatus status =
                HttpStatus.valueOf(exception.getStatusCode().value());

        String mensagem =
                exception.getReason() != null
                        ? exception.getReason()
                        : status.getReasonPhrase();

        return criarResposta(
                status,
                mensagem,
                request
        );
    }

    private ResponseEntity<ErroApi> criarResposta(
            HttpStatus status,
            String mensagem,
            HttpServletRequest request
    ) {
        ErroApi erro = new ErroApi(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                mensagem,
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(erro);
    }
}
