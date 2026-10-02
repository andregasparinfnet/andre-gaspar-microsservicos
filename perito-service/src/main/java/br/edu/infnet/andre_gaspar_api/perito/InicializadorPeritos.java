package br.edu.infnet.andre_gaspar_api.perito;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InicializadorPeritos implements CommandLineRunner {

    private final PeritoService peritoService;

    public InicializadorPeritos(PeritoService peritoService) {
        this.peritoService = peritoService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (peritoService.contar() == 0) {
            new PeritoLoader().carregar(peritoService);
        }
    }
}
