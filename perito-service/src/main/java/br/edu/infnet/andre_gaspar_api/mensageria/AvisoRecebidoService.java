package br.edu.infnet.andre_gaspar_api.mensageria;

import br.edu.infnet.andre_gaspar_api.perito.PeritoService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AvisoRecebidoService {

    private final PeritoService peritos;
    private final AvisoRecebidoRepository avisos;

    public AvisoRecebidoService(
            PeritoService peritos,
            AvisoRecebidoRepository avisos
    ) {
        this.peritos = peritos;
        this.avisos = avisos;
    }

    @Transactional(readOnly = true)
    public List<AvisoRecebido> listarPorPerito(Long peritoId) {
        peritos.obterPorId(peritoId);
        return avisos.findByPeritoIdOrderByIdDesc(peritoId);
    }
}
