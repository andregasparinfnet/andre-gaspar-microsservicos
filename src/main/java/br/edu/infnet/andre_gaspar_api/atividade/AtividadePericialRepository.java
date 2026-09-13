package br.edu.infnet.andre_gaspar_api.atividade;

import br.edu.infnet.andre_gaspar_api.atividade.AtividadePericial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AtividadePericialRepository
        extends JpaRepository<AtividadePericial, Long> {

    List<AtividadePericial> findByConcluidaOrderByPrazoAsc(
            boolean concluida
    );
}