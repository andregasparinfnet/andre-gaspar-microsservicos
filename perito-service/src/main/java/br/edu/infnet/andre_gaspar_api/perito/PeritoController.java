package br.edu.infnet.andre_gaspar_api.perito;

import br.edu.infnet.andre_gaspar_api.shared.DadosInvalidosException;
import br.edu.infnet.andre_gaspar_api.perito.dto.PeritoResponse;
import br.edu.infnet.andre_gaspar_api.perito.dto.PeritoRequest;
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
    public ResponseEntity<List<PeritoResponse>> listarTodos() {
        return ResponseEntity.ok(
                peritoService.listarTodos().stream()
                        .map(PeritoResponse::de)
                        .toList()
        );
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
    public ResponseEntity<PeritoResponse> obterPorId(
            @Parameter(description = "Identificador do perito")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                PeritoResponse.de(peritoService.obterPorId(id))
        );
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
                    description = "Já existe um perito com o e-mail"
            )
    })
    public ResponseEntity<PeritoResponse> incluir(
            @Valid @RequestBody PeritoRequest perito
    ) {
        Perito peritoIncluido = peritoService.incluir(perito.paraEntidade());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(PeritoResponse.de(peritoIncluido));
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
    public ResponseEntity<PeritoResponse> alterar(
            @Parameter(description = "Identificador do perito")
            @PathVariable Long id,
            @Valid @RequestBody PeritoRequest perito
    ) {
        if (!id.equals(perito.id())) {
            throw new DadosInvalidosException(
                    "O ID da URL deve ser igual ao ID do perito"
            );
        }

        return ResponseEntity.ok(PeritoResponse.de(peritoService.alterar(perito.paraEntidade())));
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