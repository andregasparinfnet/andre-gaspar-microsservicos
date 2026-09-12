package br.edu.infnet.andre_gaspar_api.controller;

import br.edu.infnet.andre_gaspar_api.exception.DadosInvalidosException;
import br.edu.infnet.andre_gaspar_api.model.Perito;
import br.edu.infnet.andre_gaspar_api.service.PeritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/peritos")
@Tag(
        name = "Peritos",
        description = "Operações para gerenciamento dos peritos"
)
public class PeritoController {

    private final PeritoService peritoService;

    public PeritoController(PeritoService peritoService) {
        this.peritoService = peritoService;
    }

    @GetMapping
    @Operation(summary = "Lista todos os peritos")
    @ApiResponse(
            responseCode = "200",
            description = "Lista de peritos obtida com sucesso"
    )
    public ResponseEntity<List<Perito>> listarTodos() {
        return ResponseEntity.ok(peritoService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtém um perito pelo identificador")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Perito encontrado"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Identificador inválido"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Perito não encontrado"
            )
    })
    public ResponseEntity<Perito> obterPorId(
            @Parameter(description = "Identificador do perito")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(peritoService.obterPorId(id));
    }

    @PostMapping
    @Operation(summary = "Inclui um novo perito")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Perito incluído com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados do perito inválidos"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Já existe um perito com o identificador"
            )
    })
    public ResponseEntity<Perito> incluir(
            @Valid @RequestBody Perito perito
    ) {
        Perito peritoIncluido = peritoService.incluir(perito);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(peritoIncluido);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Altera um perito existente")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Perito alterado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados ou identificador inválidos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Perito não encontrado"
            )
    })
    public ResponseEntity<Perito> alterar(
            @Parameter(description = "Identificador do perito")
            @PathVariable Long id,
            @Valid @RequestBody Perito perito
    ) {
        if (!id.equals(perito.getId())) {
            throw new DadosInvalidosException(
                    "O ID da URL deve ser igual ao ID do perito"
            );
        }

        return ResponseEntity.ok(peritoService.alterar(perito));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um perito pelo identificador")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Perito excluído com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Identificador inválido"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Perito não encontrado"
            )
    })
    public ResponseEntity<Void> excluir(
            @Parameter(description = "Identificador do perito")
            @PathVariable Long id
    ) {
        peritoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}