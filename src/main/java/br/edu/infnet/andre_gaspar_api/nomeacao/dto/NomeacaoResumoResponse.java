package br.edu.infnet.andre_gaspar_api.nomeacao.dto;

import br.edu.infnet.andre_gaspar_api.nomeacao.NomeacaoPericial;
import br.edu.infnet.andre_gaspar_api.nomeacao.StatusNomeacao;

import java.time.LocalDate;

public record NomeacaoResumoResponse(
        Long id,
        String numeroProcesso,
        LocalDate dataNomeacao,
        LocalDate dataLimite,
        int prazoEmDias,
        StatusNomeacao status,
        PeritoResumoResponse perito
) {

    public static NomeacaoResumoResponse de(
            NomeacaoPericial nomeacao
    ) {
        return new NomeacaoResumoResponse(
                nomeacao.getId(),
                nomeacao.getNumeroProcesso(),
                nomeacao.getDataNomeacao(),
                nomeacao.getDataLimite(),
                nomeacao.getPrazoEmDias(),
                nomeacao.getStatus(),
                PeritoResumoResponse.de(nomeacao.getPerito())
        );
    }
}