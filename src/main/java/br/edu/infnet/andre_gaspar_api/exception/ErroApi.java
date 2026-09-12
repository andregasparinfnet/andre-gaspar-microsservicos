package br.edu.infnet.andre_gaspar_api.exception;

import java.time.LocalDateTime;

public record ErroApi(
        LocalDateTime dataHora,
        int status,
        String erro,
        String mensagem,
        String caminho
) {
}