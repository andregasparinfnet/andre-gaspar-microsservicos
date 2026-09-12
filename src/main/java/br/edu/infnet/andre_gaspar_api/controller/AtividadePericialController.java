package br.edu.infnet.andre_gaspar_api.controller;

import br.edu.infnet.andre_gaspar_api.exception.DadosInvalidosException;
import br.edu.infnet.andre_gaspar_api.model.AtividadePericial;
import br.edu.infnet.andre_gaspar_api.service.AtividadePericialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/atividades")
@Tag(
        name = "Atividades periciais",
        description = "Operações para gerenciamento das atividades"
)
public class AtividadePericialController {

    private final AtividadePericialService atividadeService;

    public AtividadePericialController(
            AtividadePericialService atividadeService
    ) {
        this.atividadeService = atividadeService;
    }

    @GetMapping
    @Operation(summary = "Lista todas as atividades periciais")
    @ApiResponse(
            responseCode = "200",
            description = "Lista de atividades obtida com sucesso"
    )
    public ResponseEntity<List<AtividadePericial>> listarTodos() {
        return ResponseEntity.ok(atividadeService.listarTodos());
    }

    @GetMapping("/filtro")
    @Operation(
            summary = "Filtra atividades pela situação de conclusão",
            description = "Retorna as atividades ordenadas pelo prazo"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Atividades filtradas com sucesso"
    )
    public ResponseEntity<List<AtividadePericial>>
    listarPorConclusao(
            @Parameter(
                    description = "Indica se a atividade está concluída",
                    required = true
            )
            @RequestParam boolean concluida
    ) {
        return ResponseEntity.ok(
                atividadeService
                        .listarPorConclusaoOrdenadasPorPrazo(
                                concluida
                        )
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtém uma atividade pelo identificador")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Atividade encontrada"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Identificador inválido"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Atividade não encontrada"
            )
    })
    public ResponseEntity<AtividadePericial> obterPorId(
            @Parameter(description = "Identificador da atividade")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(atividadeService.obterPorId(id));
    }

    @PostMapping
    @Operation(
            summary = "Inclui uma atividade em uma nomeação pericial"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Atividade incluída com sucesso"
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
    public ResponseEntity<AtividadePericial> incluir(
            @Parameter(
                    description = "Identificador da nomeação",
                    required = true
            )
            @RequestParam Long nomeacaoId,
            @Valid @RequestBody AtividadePericial atividade
    ) {
        AtividadePericial atividadeIncluida =
                atividadeService.incluirNaNomeacao(
                        nomeacaoId,
                        atividade
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(atividadeIncluida);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Altera uma atividade pericial existente")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Atividade alterada com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados ou identificador inválidos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Atividade não encontrada"
            )
    })
    public ResponseEntity<AtividadePericial> alterar(
            @Parameter(description = "Identificador da atividade")
            @PathVariable Long id,
            @Valid @RequestBody AtividadePericial atividade
    ) {
        if (!id.equals(atividade.getId())) {
            throw new DadosInvalidosException(
                    "O ID da URL deve ser igual ao ID da atividade"
            );
        }

        return ResponseEntity.ok(
                atividadeService.alterar(atividade)
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui uma atividade pelo identificador")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Atividade excluída com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Identificador inválido"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Atividade não encontrada"
            )
    })
    public ResponseEntity<Void> excluir(
            @Parameter(description = "Identificador da atividade")
            @PathVariable Long id
    ) {
        atividadeService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}