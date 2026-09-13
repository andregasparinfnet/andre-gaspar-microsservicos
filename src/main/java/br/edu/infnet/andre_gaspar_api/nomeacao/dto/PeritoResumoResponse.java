package br.edu.infnet.andre_gaspar_api.nomeacao.dto;

import br.edu.infnet.andre_gaspar_api.perito.Perito;

public record PeritoResumoResponse(
        Long id,
        String nome,
        String email
) {

    public static PeritoResumoResponse de(Perito perito) {
        if (perito == null) {
            return null;
        }

        return new PeritoResumoResponse(
                perito.getId(),
                perito.getNome(),
                perito.getEmail()
        );
    }
}