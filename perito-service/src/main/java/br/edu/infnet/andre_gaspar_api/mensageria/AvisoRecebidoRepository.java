package br.edu.infnet.andre_gaspar_api.mensageria;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvisoRecebidoRepository
        extends JpaRepository<AvisoRecebido, Long> {

    boolean existsByAvisoId(String avisoId);

    List<AvisoRecebido> findByPeritoIdOrderByIdDesc(Long peritoId);
}
