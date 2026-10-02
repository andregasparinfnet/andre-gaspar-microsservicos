package br.edu.infnet.andre_gaspar_api.perito.dto;

import br.edu.infnet.andre_gaspar_api.perito.Perito;

public record PeritoResponse(
        Long id,
        String nome,
        String email
) {
    public static PeritoResponse de(Perito perito) {
        return new PeritoResponse(
                perito.getId(),
                perito.getNome(),
                perito.getEmail()
        );
    }
}
