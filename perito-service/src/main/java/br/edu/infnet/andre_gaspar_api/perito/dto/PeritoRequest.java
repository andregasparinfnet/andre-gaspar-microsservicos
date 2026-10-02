package br.edu.infnet.andre_gaspar_api.perito.dto;

import br.edu.infnet.andre_gaspar_api.perito.Perito;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PeritoRequest(
        Long id,

        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 3, max = 120,
                message = "O nome deve possuir entre 3 e 120 caracteres")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "O e-mail deve possuir um formato válido")
        @Size(max = 160,
                message = "O e-mail deve possuir no máximo 160 caracteres")
        String email
) {
    public Perito paraEntidade() {
        return new Perito(id, nome, email);
    }
}
