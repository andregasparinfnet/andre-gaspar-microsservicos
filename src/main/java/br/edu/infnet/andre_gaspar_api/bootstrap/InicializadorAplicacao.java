package br.edu.infnet.andre_gaspar_api.bootstrap;

import br.edu.infnet.andre_gaspar_api.enums.StatusNomeacao;
import br.edu.infnet.andre_gaspar_api.loader.AtividadeLoader;
import br.edu.infnet.andre_gaspar_api.loader.NomeacaoLoader;
import br.edu.infnet.andre_gaspar_api.loader.PeritoLoader;
import br.edu.infnet.andre_gaspar_api.model.AtividadePericial;
import br.edu.infnet.andre_gaspar_api.model.NomeacaoPericial;
import br.edu.infnet.andre_gaspar_api.model.Perito;
import br.edu.infnet.andre_gaspar_api.service.AtividadePericialService;
import br.edu.infnet.andre_gaspar_api.service.NomeacaoPericialService;
import br.edu.infnet.andre_gaspar_api.service.PeritoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class InicializadorAplicacao implements CommandLineRunner {

    private final PeritoService peritoService;
    private final NomeacaoPericialService nomeacaoService;
    private final AtividadePericialService atividadeService;

    public InicializadorAplicacao(
            PeritoService peritoService,
            NomeacaoPericialService nomeacaoService,
            AtividadePericialService atividadeService
    ) {
        this.peritoService = peritoService;
        this.nomeacaoService = nomeacaoService;
        this.atividadeService = atividadeService;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        carregarDadosIniciaisSeNecessario();

        List<Perito> peritos = peritoService.listarTodos();
        List<NomeacaoPericial> nomeacoes =
                nomeacaoService.listarTodos();
        List<AtividadePericial> atividades =
                atividadeService.listarTodos();

        System.out.println();
        System.out.println("========================================");
        System.out.println("  SISTEMA DE GESTAO DE PERICIAS");
        System.out.println("  ETAPA 4 - SPRING DATA JPA");
        System.out.println("========================================");

        for (Perito perito : peritos) {
            System.out.println();
            System.out.println(perito);

            for (NomeacaoPericial nomeacao
                    : perito.getNomeacoes()) {

                System.out.println("  " + nomeacao);
                System.out.println(
                        "    " + nomeacao.getHonorarios()
                );

                for (AtividadePericial atividade
                        : nomeacao.getAtividades()) {
                    System.out.println("    " + atividade);
                }
            }
        }

        System.out.println();
        System.out.println("------------- RESUMO -------------------");
        System.out.println(
                "Peritos persistidos: " + peritos.size()
        );
        System.out.println(
                "Nomeacoes persistidas: " + nomeacoes.size()
        );
        System.out.println(
                "Atividades persistidas: " + atividades.size()
        );

        System.out.println();
        System.out.println(
                "------- CONSULTAS SPRING DATA -----------"
        );

        System.out.println(
                "Nomeacoes recebidas: "
                        + nomeacaoService
                        .listarPorStatus(StatusNomeacao.RECEBIDA)
                        .size()
        );

        System.out.println(
                "Numeros dos processos: "
                        + nomeacaoService.listarNumerosProcessos()
        );

        System.out.println("Nomeacoes ordenadas por prazo:");

        for (NomeacaoPericial nomeacao
                : nomeacaoService.listarOrdenadasPorPrazo()) {
            System.out.println(
                    "  " + nomeacao.getDataLimite()
                            + " - "
                            + nomeacao.getNumeroProcesso()
            );
        }

        NomeacaoPericial nomeacaoEncontrada =
                nomeacaoService.obterPorNumeroProcesso(
                        "0000001-00.2026.8.00.0001"
                );

        System.out.println(
                "Busca por numero: "
                        + nomeacaoEncontrada.getNumeroProcesso()
        );

        System.out.println("========================================");
        System.out.println();
    }

    private void carregarDadosIniciaisSeNecessario()
            throws Exception {

        if (peritoService.contar() > 0
                || nomeacaoService.contar() > 0
                || atividadeService.contar() > 0) {

            System.out.println(
                    "Banco de dados já possui registros. "
                            + "Carga inicial não executada."
            );
            return;
        }

        PeritoLoader peritoLoader = new PeritoLoader();
        NomeacaoLoader nomeacaoLoader = new NomeacaoLoader();
        AtividadeLoader atividadeLoader = new AtividadeLoader();

        peritoLoader.carregar(peritoService);

        nomeacaoLoader.carregar(
                peritoLoader,
                nomeacaoService
        );

        atividadeLoader.carregar(
                nomeacaoLoader,
                atividadeService
        );

        System.out.println(
                "Dados iniciais persistidos no banco de dados."
        );
    }
}