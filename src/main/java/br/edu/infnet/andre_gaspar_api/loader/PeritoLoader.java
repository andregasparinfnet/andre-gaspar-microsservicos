package br.edu.infnet.andre_gaspar_api.loader;

import br.edu.infnet.andre_gaspar_api.exception.DadosInvalidosException;
import br.edu.infnet.andre_gaspar_api.model.Perito;
import br.edu.infnet.andre_gaspar_api.service.PeritoService;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PeritoLoader {

    private final Map<Long, Perito> peritosPorIdOrigem =
            new HashMap<>();

    public List<Perito> carregar(PeritoService peritoService)
            throws IOException {

        ClassPathResource arquivo =
                new ClassPathResource("dados/peritos.txt");

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
                String nome = campos[1];
                String email = campos[2];

                Perito perito = new Perito(nome, email);

                Perito peritoPersistido =
                        peritoService.incluir(perito);

                peritosPorIdOrigem.put(
                        idOrigem,
                        peritoPersistido
                );
            }
        }

        return peritoService.listarTodos();
    }

    public Perito obterPorIdOrigem(Long idOrigem) {
        Perito perito = peritosPorIdOrigem.get(idOrigem);

        if (perito == null) {
            throw new DadosInvalidosException(
                    "Perito não encontrado no arquivo para o ID "
                            + idOrigem
            );
        }

        return perito;
    }
}