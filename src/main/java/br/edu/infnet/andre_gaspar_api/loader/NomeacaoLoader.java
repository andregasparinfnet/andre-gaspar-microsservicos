package br.edu.infnet.andre_gaspar_api.loader;

import br.edu.infnet.andre_gaspar_api.enums.StatusNomeacao;
import br.edu.infnet.andre_gaspar_api.exception.DadosInvalidosException;
import br.edu.infnet.andre_gaspar_api.model.HonorariosPericiais;
import br.edu.infnet.andre_gaspar_api.model.NomeacaoPericial;
import br.edu.infnet.andre_gaspar_api.model.Perito;
import br.edu.infnet.andre_gaspar_api.service.NomeacaoPericialService;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NomeacaoLoader {

    private final Map<Long, NomeacaoPericial>
            nomeacoesPorIdOrigem = new HashMap<>();

    public List<NomeacaoPericial> carregar(
            PeritoLoader peritoLoader,
            NomeacaoPericialService nomeacaoService
    ) throws IOException {

        ClassPathResource arquivo =
                new ClassPathResource("dados/nomeacoes.txt");

        try (BufferedReader leitor = new BufferedReader(
                new InputStreamReader(
                        arquivo.getInputStream(),
                        StandardCharsets.UTF_8
                )
        )) {
            leitor.readLine();

            String linha;

            while ((linha = leitor.readLine()) != null) {
                if (linha.isBlank()) {
                    continue;
                }

                String[] campos = linha.split(";", -1);

                Long idOrigem = Long.valueOf(campos[0]);
                Long peritoIdOrigem =
                        Long.valueOf(campos[1]);

                String numeroProcesso = campos[2];

                LocalDate dataNomeacao =
                        LocalDate.parse(campos[3]);

                int prazoEmDias =
                        Integer.parseInt(campos[4]);

                StatusNomeacao status =
                        StatusNomeacao.valueOf(campos[5]);

                BigDecimal valorProposto =
                        new BigDecimal(campos[6]);

                BigDecimal valorFixado =
                        new BigDecimal(campos[7]);

                boolean depositado =
                        Boolean.parseBoolean(campos[8]);

                BigDecimal valorRecebido =
                        new BigDecimal(campos[9]);

                HonorariosPericiais honorarios =
                        new HonorariosPericiais(valorProposto);

                honorarios.registrarValorFixado(valorFixado);

                if (depositado) {
                    honorarios.registrarDeposito();
                }

                if (valorRecebido.compareTo(
                        BigDecimal.ZERO
                ) > 0) {
                    honorarios.registrarRecebimento(
                            valorRecebido
                    );
                }

                NomeacaoPericial nomeacao =
                        new NomeacaoPericial(
                                numeroProcesso,
                                dataNomeacao,
                                prazoEmDias,
                                honorarios
                        );

                nomeacao.alterarStatus(status);

                Perito perito =
                        peritoLoader.obterPorIdOrigem(
                                peritoIdOrigem
                        );

                nomeacao.associarPerito(perito);

                NomeacaoPericial nomeacaoPersistida =
                        nomeacaoService.incluir(nomeacao);

                perito.adicionarNomeacao(
                        nomeacaoPersistida
                );

                nomeacoesPorIdOrigem.put(
                        idOrigem,
                        nomeacaoPersistida
                );
            }
        }

        return nomeacaoService.listarTodos();
    }

    public NomeacaoPericial obterPorIdOrigem(
            Long idOrigem
    ) {
        NomeacaoPericial nomeacao =
                nomeacoesPorIdOrigem.get(idOrigem);

        if (nomeacao == null) {
            throw new DadosInvalidosException(
                    "Nomeação não encontrada no arquivo para o ID "
                            + idOrigem
            );
        }

        return nomeacao;
    }
}