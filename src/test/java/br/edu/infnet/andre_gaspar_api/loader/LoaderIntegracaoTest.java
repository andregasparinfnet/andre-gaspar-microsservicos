package br.edu.infnet.andre_gaspar_api.loader;

import br.edu.infnet.andre_gaspar_api.model.AtividadePericial;
import br.edu.infnet.andre_gaspar_api.model.NomeacaoPericial;
import br.edu.infnet.andre_gaspar_api.model.Perito;
import br.edu.infnet.andre_gaspar_api.service.AtividadePericialService;
import br.edu.infnet.andre_gaspar_api.service.NomeacaoPericialService;
import br.edu.infnet.andre_gaspar_api.service.PeritoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class LoaderIntegracaoTest {

    @Autowired
    private PeritoService peritoService;

    @Autowired
    private NomeacaoPericialService nomeacaoService;

    @Autowired
    private AtividadePericialService atividadeService;

    @Test
    void deveCarregarArquivosNoBancoEPreservarRelacionamentos() {
        List<Perito> peritos =
                peritoService.listarTodos();

        List<NomeacaoPericial> nomeacoes =
                nomeacaoService.listarTodos();

        List<AtividadePericial> atividades =
                atividadeService.listarTodos();

        assertEquals(1, peritos.size());
        assertEquals(2, nomeacoes.size());
        assertEquals(4, atividades.size());

        Perito perito = peritos.getFirst();

        assertNotNull(perito.getId());
        assertEquals(2, perito.getNomeacoes().size());

        NomeacaoPericial primeiraNomeacao =
                nomeacaoService.obterPorNumeroProcesso(
                        "0000001-00.2026.8.00.0001"
                );

        NomeacaoPericial segundaNomeacao =
                nomeacaoService.obterPorNumeroProcesso(
                        "0000002-00.2026.8.00.0002"
                );

        assertNotNull(primeiraNomeacao.getPerito());
        assertEquals(
                perito.getId(),
                primeiraNomeacao.getPerito().getId()
        );

        assertEquals(
                3,
                primeiraNomeacao.getAtividades().size()
        );

        assertEquals(
                1,
                segundaNomeacao.getAtividades().size()
        );

        AtividadePericial primeiraAtividade =
                primeiraNomeacao.getAtividades().getFirst();

        assertNotNull(primeiraAtividade.getNomeacao());

        assertEquals(
                primeiraNomeacao.getId(),
                primeiraAtividade.getNomeacao().getId()
        );
    }
}