package br.edu.infnet.andre_gaspar_api.repository;

import br.edu.infnet.andre_gaspar_api.enums.StatusNomeacao;
import br.edu.infnet.andre_gaspar_api.model.NomeacaoPericial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NomeacaoPericialRepository
        extends JpaRepository<NomeacaoPericial, Long> {

    List<NomeacaoPericial> findByStatus(StatusNomeacao status);

    List<NomeacaoPericial> findAllByOrderByDataLimiteAsc();

    Optional<NomeacaoPericial> findByNumeroProcesso(
            String numeroProcesso
    );

    boolean existsByNumeroProcesso(String numeroProcesso);

    boolean existsByNumeroProcessoAndIdNot(
            String numeroProcesso,
            Long id
    );
}