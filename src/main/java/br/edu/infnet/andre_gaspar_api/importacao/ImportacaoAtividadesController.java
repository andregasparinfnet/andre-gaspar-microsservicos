package br.edu.infnet.andre_gaspar_api.importacao;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.UUID;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/importacoes/atividades")
public class ImportacaoAtividadesController {

    private final JobOperator operador;
    private final Job importarAtividadesJob;

    public ImportacaoAtividadesController(
            JobOperator operador,
            Job importarAtividadesJob
    ) {
        this.operador = operador;
        this.importarAtividadesJob = importarAtividadesJob;
    }

    @PostMapping
    @Operation(summary = "Executa a importação em lote das atividades do CSV")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultado da execução do job"),
            @ApiResponse(responseCode = "422", description = "Job terminou com falha")
    })
    public ResponseEntity<ResultadoImportacao> executar() throws Exception {
        JobExecution execucao = operador.run(
                importarAtividadesJob,
                new JobParametersBuilder()
                        .addString("execucao", UUID.randomUUID().toString())
                        .toJobParameters()
        );

        long lidos = execucao.getStepExecutions().stream()
                .mapToLong(StepExecution::getReadCount)
                .sum();
        long processados = execucao.getStepExecutions().stream()
                .mapToLong(StepExecution::getWriteCount)
                .sum();

        ResultadoImportacao resultado = new ResultadoImportacao(
                execucao.getId(),
                execucao.getStatus().name(),
                lidos,
                processados
        );

        HttpStatus codigo = execucao.getStatus() == BatchStatus.FAILED
                ? HttpStatus.UNPROCESSABLE_ENTITY
                : HttpStatus.OK;
        return ResponseEntity.status(codigo).body(resultado);
    }

    public record ResultadoImportacao(
            Long execucaoId,
            String status,
            long lidos,
            long processados
    ) {
    }
}
