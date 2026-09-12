package br.edu.infnet.andre_gaspar_api.service;

import br.edu.infnet.andre_gaspar_api.enums.StatusNomeacao;
import br.edu.infnet.andre_gaspar_api.exception.EntidadeJaExistenteException;
import br.edu.infnet.andre_gaspar_api.exception.EntidadeNaoEncontradaException;
import br.edu.infnet.andre_gaspar_api.model.HonorariosPericiais;
import br.edu.infnet.andre_gaspar_api.model.NomeacaoPericial;
import br.edu.infnet.andre_gaspar_api.model.Perito;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class NomeacaoPericialServiceTest {

    @Autowired
    private NomeacaoPericialService service;

    @Autowired
    private PeritoService peritoService;

    @Test
    void deveIncluirEObterNomeacaoPorId() {
        NomeacaoPericial nomeacao = criarNomeacao(
                "PROCESSO-TESTE-INCLUSAO",
                LocalDate.of(2026, 10, 20),
                StatusNomeacao.RECEBIDA
        );

        NomeacaoPericial incluída = service.incluir(nomeacao);

        assertNotNull(incluída.getId());

        NomeacaoPericial encontrada =
                service.obterPorId(incluída.getId());

        assertEquals(
                "PROCESSO-TESTE-INCLUSAO",
                encontrada.getNumeroProcesso()
        );
    }

    @Test
    void deveAlterarEExcluirNomeacao() {
        NomeacaoPericial original = service.incluir(
                criarNomeacao(
                        "PROCESSO-TESTE-ALTERACAO",
                        LocalDate.of(2026, 10, 21),
                        StatusNomeacao.RECEBIDA
                )
        );

        NomeacaoPericial alterada = criarNomeacaoComId(
                original.getId(),
                "PROCESSO-TESTE-ALTERADO",
                LocalDate.of(2026, 10, 22),
                StatusNomeacao.ACEITA
        );

        NomeacaoPericial resultado =
                service.alterar(alterada);

        assertEquals(
                "PROCESSO-TESTE-ALTERADO",
                resultado.getNumeroProcesso()
        );

        assertEquals(
                StatusNomeacao.ACEITA,
                resultado.getStatus()
        );

        service.excluir(resultado.getId());

        assertThrows(
                EntidadeNaoEncontradaException.class,
                () -> service.obterPorId(resultado.getId())
        );
    }

    @Test
    void deveRejeitarNumeroDeProcessoDuplicado() {
        NomeacaoPericial primeira = criarNomeacao(
                "PROCESSO-TESTE-DUPLICADO",
                LocalDate.of(2026, 10, 23),
                StatusNomeacao.RECEBIDA
        );

        NomeacaoPericial segunda = criarNomeacao(
                "PROCESSO-TESTE-DUPLICADO",
                LocalDate.of(2026, 10, 24),
                StatusNomeacao.RECEBIDA
        );

        service.incluir(primeira);

        assertThrows(
                EntidadeJaExistenteException.class,
                () -> service.incluir(segunda)
        );

        assertThrows(
                EntidadeNaoEncontradaException.class,
                () -> service.obterPorId(99999L)
        );
    }

    @Test
    void deveExecutarConsultasPersonalizadas() {
        NomeacaoPericial primeira = service.incluir(
                criarNomeacao(
                        "PROCESSO-TESTE-CONSULTA-1",
                        LocalDate.of(2026, 11, 10),
                        StatusNomeacao.ACEITA
                )
        );

        NomeacaoPericial segunda = service.incluir(
                criarNomeacao(
                        "PROCESSO-TESTE-CONSULTA-2",
                        LocalDate.of(2026, 10, 10),
                        StatusNomeacao.RECEBIDA
                )
        );

        List<NomeacaoPericial> recebidas =
                service.listarPorStatus(
                        StatusNomeacao.RECEBIDA
                );

        assertTrue(
                recebidas.stream().anyMatch(nomeacao ->
                        nomeacao.getId().equals(segunda.getId())
                )
        );

        List<NomeacaoPericial> ordenadas =
                service.listarOrdenadasPorPrazo();

        int posiçãoPrimeira = índiceDaNomeacao(
                ordenadas,
                primeira.getId()
        );

        int posiçãoSegunda = índiceDaNomeacao(
                ordenadas,
                segunda.getId()
        );

        assertTrue(posiçãoSegunda < posiçãoPrimeira);

        NomeacaoPericial encontrada =
                service.obterPorNumeroProcesso(
                        "PROCESSO-TESTE-CONSULTA-1"
                );

        assertEquals(
                primeira.getId(),
                encontrada.getId()
        );

        assertTrue(
                service.listarNumerosProcessos()
                        .contains("PROCESSO-TESTE-CONSULTA-1")
        );
    }

    private int índiceDaNomeacao(
            List<NomeacaoPericial> nomeacoes,
            Long id
    ) {
        for (int índice = 0;
             índice < nomeacoes.size();
             índice++) {

            if (nomeacoes.get(índice).getId().equals(id)) {
                return índice;
            }
        }

        return -1;
    }

    private NomeacaoPericial criarNomeacao(
            String numeroProcesso,
            LocalDate dataNomeacao,
            StatusNomeacao status
    ) {
        HonorariosPericiais honorarios =
                new HonorariosPericiais(
                        new BigDecimal("1000.00")
                );

        NomeacaoPericial nomeacao =
                new NomeacaoPericial(
                        numeroProcesso,
                        dataNomeacao,
                        5,
                        honorarios
                );

        Perito perito =
                peritoService.listarTodos().getFirst();

        nomeacao.associarPerito(perito);
        nomeacao.alterarStatus(status);
        return nomeacao;
    }

    private NomeacaoPericial criarNomeacaoComId(
            Long id,
            String numeroProcesso,
            LocalDate dataNomeacao,
            StatusNomeacao status
    ) {
        HonorariosPericiais honorarios =
                new HonorariosPericiais(
                        new BigDecimal("1000.00")
                );

        NomeacaoPericial nomeacao =
                new NomeacaoPericial(
                        id,
                        numeroProcesso,
                        dataNomeacao,
                        5,
                        honorarios
                );

        Perito perito =
                peritoService.listarTodos().getFirst();
        nomeacao.associarPerito(perito);
        nomeacao.alterarStatus(status);
        return nomeacao;
    }
}