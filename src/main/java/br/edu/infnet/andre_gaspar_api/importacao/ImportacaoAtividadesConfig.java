package br.edu.infnet.andre_gaspar_api.importacao;

import br.edu.infnet.andre_gaspar_api.atividade.AtividadePericial;
import br.edu.infnet.andre_gaspar_api.atividade.AtividadePericialRepository;
import br.edu.infnet.andre_gaspar_api.nomeacao.NomeacaoPericial;
import br.edu.infnet.andre_gaspar_api.nomeacao.NomeacaoPericialRepository;
import java.time.LocalDate;
import java.util.Locale;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ImportacaoAtividadesConfig {

    @Bean
    @StepScope
    FlatFileItemReader<AtividadeCsv> leitorAtividades(
            @Value("${batch.atividades.arquivo}") Resource arquivo
    ) {
        return new FlatFileItemReaderBuilder<AtividadeCsv>()
                .name("leitorAtividadesCsv")
                .resource(arquivo)
                .linesToSkip(1)
                .delimited()
                .delimiter(";")
                .names(
                        "codigo", "numeroProcesso", "descricao",
                        "prazo", "horasEstimadas"
                )
                .fieldSetMapper(campos -> new AtividadeCsv(
                        campos.readString("codigo"),
                        campos.readString("numeroProcesso"),
                        campos.readString("descricao"),
                        campos.readString("prazo"),
                        campos.readString("horasEstimadas")
                ))
                .build();
    }

    @Bean
    ItemProcessor<AtividadeCsv, AtividadeImportada> processadorAtividades() {
        return linha -> {
            String codigo = linha.codigo().strip().toUpperCase(Locale.ROOT);
            String processo = linha.numeroProcesso().strip();
            String descricao = linha.descricao().strip().replaceAll("\\s+", " ");

            if (codigo.isEmpty() || codigo.length() > 64 || processo.isEmpty()
                    || descricao.length() < 3 || descricao.length() > 200) {
                throw new IllegalArgumentException(
                        "Atividade inválida no CSV: " + codigo
                );
            }

            LocalDate prazo = LocalDate.parse(linha.prazo().strip());
            double horas = Double.parseDouble(linha.horasEstimadas().strip());
            if (!Double.isFinite(horas) || horas <= 0) {
                throw new IllegalArgumentException(
                        "Horas inválidas no CSV: " + codigo
                );
            }

            return new AtividadeImportada(
                    codigo, processo, descricao, prazo, horas
            );
        };
    }

    @Bean
    ItemWriter<AtividadeImportada> gravadorAtividades(
            AtividadePericialRepository atividades,
            NomeacaoPericialRepository nomeacoes
    ) {
        return itens -> {
            for (AtividadeImportada item : itens) {
                if (atividades.existsByCodigoImportacao(item.codigo())) {
                    continue;
                }

                NomeacaoPericial nomeacao = nomeacoes
                        .findByNumeroProcesso(item.numeroProcesso())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Processo inexistente no CSV: "
                                        + item.numeroProcesso()
                        ));

                AtividadePericial atividade = new AtividadePericial(
                        item.descricao(), item.prazo(), item.horasEstimadas()
                );
                atividade.associarNomeacao(nomeacao);
                atividade.definirCodigoImportacao(item.codigo());
                atividades.save(atividade);
            }
        };
    }

    @Bean
    Step importarAtividadesStep(
            JobRepository repositorio,
            PlatformTransactionManager transacoes,
            FlatFileItemReader<AtividadeCsv> leitorAtividades,
            ItemProcessor<AtividadeCsv, AtividadeImportada> processadorAtividades,
            ItemWriter<AtividadeImportada> gravadorAtividades
    ) {
        return new StepBuilder("importarAtividades", repositorio)
                .<AtividadeCsv, AtividadeImportada>chunk(5)
                .transactionManager(transacoes)
                .reader(leitorAtividades)
                .processor(processadorAtividades)
                .writer(gravadorAtividades)
                .build();
    }

    @Bean
    Job importarAtividadesJob(
            JobRepository repositorio,
            Step importarAtividadesStep
    ) {
        return new JobBuilder("importarAtividadesCsv", repositorio)
                .start(importarAtividadesStep)
                .build();
    }
}
