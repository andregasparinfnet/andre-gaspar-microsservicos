package br.edu.infnet.andre_gaspar_api.repository;

import br.edu.infnet.andre_gaspar_api.model.AtividadePericial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AtividadePericialRepository
        extends JpaRepository<AtividadePericial, Long> {

    List<AtividadePericial> findByConcluidaOrderByPrazoAsc(
            boolean concluida
    );
}