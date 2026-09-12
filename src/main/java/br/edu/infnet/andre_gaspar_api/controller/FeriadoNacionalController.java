package br.edu.infnet.andre_gaspar_api.controller;

import br.edu.infnet.andre_gaspar_api.dto.FeriadoNacional;
import br.edu.infnet.andre_gaspar_api.service.FeriadoNacionalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/feriados")
@Tag(
        name = "Feriados nacionais",
        description = "Integração externa com a BrasilAPI usando OpenFeign"
)
public class FeriadoNacionalController {

    private final FeriadoNacionalService feriadoService;

    public FeriadoNacionalController(
            FeriadoNacionalService feriadoService
    ) {
        this.feriadoService = feriadoService;
    }

    @GetMapping("/{ano}")
    @Operation(
            summary = "Consultar feriados nacionais",
            description = "Consulta os feriados nacionais de um ano na BrasilAPI."
    )
    public List<FeriadoNacional> listarPorAno(
            @PathVariable int ano
    ) {
        return feriadoService.listarPorAno(ano);
    }
}
