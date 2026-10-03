package br.edu.infnet.andre_gaspar_api.importacao;

import java.time.LocalDate;

public record AtividadeImportada(
        String codigo,
        String numeroProcesso,
        String descricao,
        LocalDate prazo,
        double horasEstimadas
) {
}
