package br.edu.infnet.andre_gaspar_api.mensageria;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/peritos")
public class AvisoRecebidoController {

    private final AvisoRecebidoService servico;

    public AvisoRecebidoController(AvisoRecebidoService servico) {
        this.servico = servico;
    }

    @GetMapping("/{id}/avisos")
    @Operation(summary = "Lista os avisos registrados para um perito")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Avisos consultados"),
            @ApiResponse(responseCode = "404", description = "Perito não encontrado")
    })
    public List<AvisoRecebido> listar(@PathVariable Long id) {
        return servico.listarPorPerito(id);
    }
}
