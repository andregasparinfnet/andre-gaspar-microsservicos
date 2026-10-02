package br.edu.infnet.andre_gaspar_api.loader;

import br.edu.infnet.andre_gaspar_api.atividade.AtividadePericial;
import br.edu.infnet.andre_gaspar_api.atividade.AtividadePericialService;
import br.edu.infnet.andre_gaspar_api.nomeacao.NomeacaoPericial;
import br.edu.infnet.andre_gaspar_api.nomeacao.NomeacaoPericialService;
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
    private NomeacaoPericialService nomeacaoService;

    @Autowired
    private AtividadePericialService atividadeService;

    @Test
    void deveCarregarArquivosEPreservarReferenciasPorId() {
        List<NomeacaoPericial> nomeacoes =
                nomeacaoService.listarTodos();

        List<AtividadePericial> atividades =
                atividadeService.listarTodos();

        assertEquals(2, nomeacoes.size());
        assertEquals(4, atividades.size());

        NomeacaoPericial primeiraNomeacao =
                nomeacaoService.obterPorNumeroProcesso(
                        "0000001-00.2026.8.00.0001"
                );

        NomeacaoPericial segundaNomeacao =
                nomeacaoService.obterPorNumeroProcesso(
                        "0000002-00.2026.8.00.0002"
                );

        assertEquals(1L, primeiraNomeacao.getPeritoId());
        assertEquals(1L, segundaNomeacao.getPeritoId());

        assertEquals(3, primeiraNomeacao.getAtividades().size());
        assertEquals(1, segundaNomeacao.getAtividades().size());

        AtividadePericial primeiraAtividade =
                primeiraNomeacao.getAtividades().getFirst();

        assertNotNull(primeiraAtividade.getNomeacao());
        assertEquals(
                primeiraNomeacao.getId(),
                primeiraAtividade.getNomeacao().getId()
        );
    }
}
