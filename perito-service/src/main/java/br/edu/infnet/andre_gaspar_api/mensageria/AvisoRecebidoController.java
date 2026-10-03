package br.edu.infnet.andre_gaspar_api.mensageria;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/peritos")
public class AvisoRecebidoController {

    private final AvisoRecebidoService servico;

    public AvisoRecebidoController(AvisoRecebidoService servico) {
        this.servico = servico;
    }

    @GetMapping("/{id}/avisos")
    public List<AvisoRecebido> listar(@PathVariable Long id) {
        return servico.listarPorPerito(id);
    }
}
