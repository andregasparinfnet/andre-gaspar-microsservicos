package br.edu.infnet.andre_gaspar_api.service;

import br.edu.infnet.andre_gaspar_api.client.BrasilApiClient;
import br.edu.infnet.andre_gaspar_api.dto.FeriadoNacional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class FeriadoNacionalService {

    private final BrasilApiClient brasilApiClient;

    public FeriadoNacionalService(
            BrasilApiClient brasilApiClient
    ) {
        this.brasilApiClient = brasilApiClient;
    }

    public List<FeriadoNacional> listarPorAno(int ano) {
        if (ano < 1900 || ano > 2100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O ano deve estar entre 1900 e 2100."
            );
        }

        return brasilApiClient.listarFeriados(ano);
    }
}
