package br.edu.infnet.andre_gaspar_api.bootstrap;

import br.edu.infnet.andre_gaspar_api.atividade.AtividadeLoader;
import br.edu.infnet.andre_gaspar_api.atividade.AtividadePericial;
import br.edu.infnet.andre_gaspar_api.atividade.AtividadePericialService;
import br.edu.infnet.andre_gaspar_api.nomeacao.NomeacaoLoader;
import br.edu.infnet.andre_gaspar_api.nomeacao.NomeacaoPericial;
import br.edu.infnet.andre_gaspar_api.nomeacao.NomeacaoPericialService;
import br.edu.infnet.andre_gaspar_api.nomeacao.StatusNomeacao;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class InicializadorAplicacao implements CommandLineRunner {

    private final NomeacaoPericialService nomeacaoService;
    private final AtividadePericialService atividadeService;

    public InicializadorAplicacao(
            NomeacaoPericialService nomeacaoService,
            AtividadePericialService atividadeService
    ) {
        this.nomeacaoService = nomeacaoService;
        this.atividadeService = atividadeService;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        carregarDadosIniciaisSeNecessario();

        List<NomeacaoPericial> nomeacoes =
                nomeacaoService.listarTodos();
        List<AtividadePericial> atividades =
                atividadeService.listarTodos();

        System.out.println();
        System.out.println("========================================");
        System.out.println("  SISTEMA DE GESTAO DE PERICIAS");
        System.out.println("  ETAPA 2 - SERVIÇO DE PERITOS");
        System.out.println("========================================");

        for (NomeacaoPericial nomeacao : nomeacoes) {
            System.out.println(nomeacao);
            System.out.println("  Perito ID: " + nomeacao.getPeritoId());

            for (AtividadePericial atividade : nomeacao.getAtividades()) {
                System.out.println("  " + atividade);
            }
        }

        System.out.println();
        System.out.println("Nomeacoes persistidas: " + nomeacoes.size());
        System.out.println("Atividades persistidas: " + atividades.size());
        System.out.println(
                "Nomeacoes recebidas: "
                        + nomeacaoService
                        .listarPorStatus(StatusNomeacao.RECEBIDA)
                        .size()
        );
        System.out.println("========================================");
    }

    private void carregarDadosIniciaisSeNecessario()
            throws Exception {

        if (nomeacaoService.contar() > 0
                || atividadeService.contar() > 0) {
            System.out.println(
                    "Banco de dados já possui registros. "
                            + "Carga inicial não executada."
            );
            return;
        }

        NomeacaoLoader nomeacaoLoader = new NomeacaoLoader();
        AtividadeLoader atividadeLoader = new AtividadeLoader();

        nomeacaoLoader.carregar(nomeacaoService);
        atividadeLoader.carregar(nomeacaoLoader, atividadeService);

        System.out.println(
                "Dados iniciais de nomeações e atividades persistidos."
        );
    }
}
