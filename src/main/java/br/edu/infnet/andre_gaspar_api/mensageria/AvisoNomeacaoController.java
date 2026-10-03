package br.edu.infnet.andre_gaspar_api.mensageria;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Publica um aviso de nomeação para processamento assíncrono")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Aviso confirmado pelo broker"),
            @ApiResponse(responseCode = "404", description = "Nomeação não encontrada"),
            @ApiResponse(responseCode = "503", description = "Broker indisponível")
    })
    public ResponseEntity<AvisoNomeacaoMessage> publicar(
            @PathVariable Long id
    ) {
        return ResponseEntity.accepted()
                .body(publicador.publicar(id));
    }
}
