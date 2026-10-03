package br.edu.infnet.andre_gaspar_api.mensageria;

import br.edu.infnet.andre_gaspar_api.perito.PeritoService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/peritos")
public class AvisoRecebidoController {

    private final PeritoService peritos;
    private final AvisoRecebidoRepository avisos;

    public AvisoRecebidoController(
            PeritoService peritos,
            AvisoRecebidoRepository avisos
    ) {
        this.peritos = peritos;
        this.avisos = avisos;
    }

    @GetMapping("/{id}/avisos")
    public List<AvisoRecebido> listar(@PathVariable Long id) {
        peritos.obterPorId(id);
        return avisos.findByPeritoIdOrderByIdDesc(id);
    }
}
