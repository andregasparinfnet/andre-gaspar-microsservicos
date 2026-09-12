package br.edu.infnet.andre_gaspar_api.service;

import br.edu.infnet.andre_gaspar_api.exception.DadosInvalidosException;
import br.edu.infnet.andre_gaspar_api.model.AtividadePericial;
import br.edu.infnet.andre_gaspar_api.model.NomeacaoPericial;
import br.edu.infnet.andre_gaspar_api.repository.AtividadePericialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AtividadePericialService
        extends BaseCrudService<AtividadePericial> {

    private final AtividadePericialRepository atividadeRepository;
    private final NomeacaoPericialService nomeacaoService;

    public AtividadePericialService(
            AtividadePericialRepository atividadeRepository,
            NomeacaoPericialService nomeacaoService
    ) {
        super(atividadeRepository);
        this.atividadeRepository = atividadeRepository;
        this.nomeacaoService = nomeacaoService;
    }

    @Transactional
    public AtividadePericial incluirNaNomeacao(
            Long nomeacaoId,
            AtividadePericial atividade
    ) {
        NomeacaoPericial nomeacao =
                nomeacaoService.obterPorId(nomeacaoId);

        atividade.associarNomeacao(nomeacao);

        return incluir(atividade);
    }

    @Override
    @Transactional
    public AtividadePericial alterar(
            AtividadePericial dadosAtualizados
    ) {
        if (dadosAtualizados == null) {
            throw new DadosInvalidosException(
                    "Os dados da atividade são obrigatórios"
            );
        }

        validarId(dadosAtualizados.getId());

        AtividadePericial atividadePersistida =
                obterPorId(dadosAtualizados.getId());

        atividadePersistida.atualizarDados(
                dadosAtualizados
        );

        validarDadosEspecificos(atividadePersistida);

        return atividadeRepository.save(
                atividadePersistida
        );
    }

    @Transactional(readOnly = true)
    public List<AtividadePericial>
    listarPorConclusaoOrdenadasPorPrazo(
            boolean concluida
    ) {
        return atividadeRepository
                .findByConcluidaOrderByPrazoAsc(concluida);
    }

    @Override
    protected void validarDadosEspecificos(
            AtividadePericial atividade
    ) {
        if (atividade == null) {
            throw new DadosInvalidosException(
                    "Os dados da atividade são obrigatórios"
            );
        }

        if (atividade.getDescricao() == null
                || atividade.getDescricao().isBlank()) {
            throw new DadosInvalidosException(
                    "A descrição da atividade é obrigatória"
            );
        }

        if (atividade.getPrazo() == null) {
            throw new DadosInvalidosException(
                    "O prazo da atividade é obrigatório"
            );
        }

        if (atividade.getHorasEstimadas() <= 0) {
            throw new DadosInvalidosException(
                    "As horas estimadas devem ser positivas"
            );
        }

        if (atividade.getNomeacao() == null) {
            throw new DadosInvalidosException(
                    "A nomeação da atividade é obrigatória"
            );
        }
    }
}
