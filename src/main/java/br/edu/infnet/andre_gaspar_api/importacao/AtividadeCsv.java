package br.edu.infnet.andre_gaspar_api.importacao;

public record AtividadeCsv(
        String codigo,
        String numeroProcesso,
        String descricao,
        String prazo,
        String horasEstimadas
) {
}
