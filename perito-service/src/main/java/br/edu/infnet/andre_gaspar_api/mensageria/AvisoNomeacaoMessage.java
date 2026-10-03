package br.edu.infnet.andre_gaspar_api.mensageria;

import java.util.UUID;

public record AvisoNomeacaoMessage(
        UUID avisoId,
        Long nomeacaoId,
        Long peritoId,
        String numeroProcesso
) {
}
