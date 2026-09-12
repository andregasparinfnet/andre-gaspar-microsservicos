package br.edu.infnet.andre_gaspar_api.calendario;

import br.edu.infnet.andre_gaspar_api.calendario.FeriadoNacional;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "brasilApiFeriados",
        url = "${integracoes.brasil-api.url}"
)
public interface BrasilApiClient {

    @GetMapping("/api/feriados/v1/{ano}")
    List<FeriadoNacional> listarFeriados(
            @PathVariable("ano") int ano
    );
}
