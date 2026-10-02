package br.edu.infnet.andre_gaspar_api.nomeacao.client;

import br.edu.infnet.andre_gaspar_api.nomeacao.dto.PeritoResumoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "perito-service",
        url = "${servicos.perito.url}"
)
public interface PeritoClient {

    @GetMapping("/api/peritos/{id}")
    PeritoResumoResponse obterPorId(@PathVariable("id") Long id);
}
