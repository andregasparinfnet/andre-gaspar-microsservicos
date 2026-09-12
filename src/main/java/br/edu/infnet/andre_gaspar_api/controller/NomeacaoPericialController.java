package br.edu.infnet.andre_gaspar_api.controller;

import br.edu.infnet.andre_gaspar_api.enums.StatusNomeacao;

import br.edu.infnet.andre_gaspar_api.exception.DadosInvalidosException;
import br.edu.infnet.andre_gaspar_api.model.NomeacaoPericial;
import br.edu.infnet.andre_gaspar_api.service.NomeacaoPericialService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/nomeacoes")
@Tag(
        name = "Nomeações periciais",
        description = "Operações para gerenciamento das nomeações"
)
public class NomeacaoPericialController {

    private final NomeacaoPericialService nomeacaoService;

    public NomeacaoPericialController(
            NomeacaoPericialService nomeacaoService
    ) {
        this.nomeacaoService = nomeacaoService;
    }

    @GetMapping
    @Operation(summary = "Lista todas as nomeações periciais")
    @ApiResponse(
            responseCode = "200",
            description = "Lista de nomeações obtida com sucesso"
    )
    public ResponseEntity<List<NomeacaoPericial>> listarTodos() {
        return ResponseEntity.ok(nomeacaoService.listarTodos());
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Lista nomeações filtradas por status")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Nomeações filtradas com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Status informado é inválido"
            )
    })
    public ResponseEntity<List<NomeacaoPericial>> listarPorStatus(
            @Parameter(description = "Status da nomeação")
            @PathVariable StatusNomeacao status
    ) {
        return ResponseEntity.ok(
                nomeacaoService.listarPorStatus(status)
        );
    }

    @GetMapping("/ordenadas-por-prazo")
    @Operation(summary = "Lista nomeações ordenadas pelo prazo")
    @ApiResponse(
            responseCode = "200",
            description = "Nomeações ordenadas com sucesso"
    )
    public ResponseEntity<List<NomeacaoPericial>>
    listarOrdenadasPorPrazo() {
        return ResponseEntity.ok(
                nomeacaoService.listarOrdenadasPorPrazo()
        );
    }

    @GetMapping("/processo")
    @Operation(summary = "Obtém uma nomeação pelo número do processo")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Nomeação encontrada"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Número do processo inválido"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Nomeação não encontrada"
            )
    })
    public ResponseEntity<NomeacaoPericial> obterPorNumeroProcesso(
            @Parameter(
                    description = "Número do processo",
                    required = true
            )
            @RequestParam String numeroProcesso
    ) {
        return ResponseEntity.ok(
                nomeacaoService.obterPorNumeroProcesso(
                        numeroProcesso
                )
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtém uma nomeação pelo identificador")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Nomeação encontrada"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Identificador inválido"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Nomeação não encontrada"
            )
    })
    public ResponseEntity<NomeacaoPericial> obterPorId(
            @Parameter(description = "Identificador da nomeação")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(nomeacaoService.obterPorId(id));
    }

    @PostMapping
    @Operation(summary = "Inclui uma nova nomeação pericial")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Nomeação incluída com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados da nomeação inválidos"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Já existe uma nomeação para o processo"
            )
    })
    public ResponseEntity<NomeacaoPericial> incluir(
            @Parameter(
                    description = "Identificador do perito",
                    required = true
            )
            @RequestParam Long peritoId,
            @Valid @RequestBody NomeacaoPericial nomeacao
    ) {
        NomeacaoPericial nomeacaoIncluida =
                nomeacaoService.incluirParaPerito(
                        peritoId,
                        nomeacao
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nomeacaoIncluida);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Altera uma nomeação pericial existente")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Nomeação alterada com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados ou identificador inválidos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Nomeação não encontrada"
            )
    })
    public ResponseEntity<NomeacaoPericial> alterar(
            @Parameter(description = "Identificador da nomeação")
            @PathVariable Long id,
            @Valid @RequestBody NomeacaoPericial nomeacao
    ) {
        if (!id.equals(nomeacao.getId())) {
            throw new DadosInvalidosException(
                    "O ID da URL deve ser igual ao ID da nomeação"
            );
        }

        return ResponseEntity.ok(
                nomeacaoService.alterar(nomeacao)
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui uma nomeação pelo identificador")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Nomeação excluída com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Identificador inválido"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Nomeação não encontrada"
            )
    })
    public ResponseEntity<Void> excluir(
            @Parameter(description = "Identificador da nomeação")
            @PathVariable Long id
    ) {
        nomeacaoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}