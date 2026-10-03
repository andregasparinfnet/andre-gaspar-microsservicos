package br.edu.infnet.andre_gaspar_api.mensageria;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/nomeacoes")
public class AvisoNomeacaoController {

    private final PublicadorAvisos publicador;

    public AvisoNomeacaoController(PublicadorAvisos publicador) {
        this.publicador = publicador;
    }

    @PostMapping("/{id}/avisos")
    public ResponseEntity<AvisoNomeacaoMessage> publicar(
            @PathVariable Long id
    ) {
        return ResponseEntity.accepted()
                .body(publicador.publicar(id));
    }
}
