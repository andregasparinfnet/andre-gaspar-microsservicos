package br.edu.infnet.andre_gaspar_api.service;

import br.edu.infnet.andre_gaspar_api.enums.StatusNomeacao;
import br.edu.infnet.andre_gaspar_api.exception.DadosInvalidosException;
import br.edu.infnet.andre_gaspar_api.exception.EntidadeJaExistenteException;
import br.edu.infnet.andre_gaspar_api.exception.EntidadeNaoEncontradaException;
import br.edu.infnet.andre_gaspar_api.model.NomeacaoPericial;
import br.edu.infnet.andre_gaspar_api.model.Perito;
import br.edu.infnet.andre_gaspar_api.repository.NomeacaoPericialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NomeacaoPericialService
        extends BaseCrudService<NomeacaoPericial> {

    private final NomeacaoPericialRepository nomeacaoRepository;
    private final PeritoService peritoService;

    public NomeacaoPericialService(
            NomeacaoPericialRepository nomeacaoRepository,
            PeritoService peritoService
    ) {
        super(nomeacaoRepository);
        this.nomeacaoRepository = nomeacaoRepository;
        this.peritoService = peritoService;
    }

    @Override
    @Transactional
    public NomeacaoPericial incluir(
            NomeacaoPericial nomeacao
    ) {
        validarDadosEspecificos(nomeacao);
        nomeacao.recalcularDataLimite();

        if (nomeacaoRepository.existsByNumeroProcesso(
                nomeacao.getNumeroProcesso()
        )) {
            throw new EntidadeJaExistenteException(
                    "Já existe uma nomeação para o processo "
                            + nomeacao.getNumeroProcesso()
            );
        }

        return super.incluir(nomeacao);
    }

    @Transactional
    public NomeacaoPericial incluirParaPerito(
            Long peritoId,
            NomeacaoPericial nomeacao
    ) {
        Perito perito = peritoService.obterPorId(peritoId);
        nomeacao.associarPerito(perito);

        return incluir(nomeacao);
    }

    @Override
    @Transactional
    public NomeacaoPericial alterar(
            NomeacaoPericial dadosAtualizados
    ) {
        if (dadosAtualizados == null) {
            throw new DadosInvalidosException(
                    "Os dados da nomeação são obrigatórios"
            );
        }

        validarId(dadosAtualizados.getId());

        NomeacaoPericial nomeacaoPersistida =
                obterPorId(dadosAtualizados.getId());

        if (nomeacaoRepository
                .existsByNumeroProcessoAndIdNot(
                        dadosAtualizados.getNumeroProcesso(),
                        dadosAtualizados.getId()
                )) {
            throw new EntidadeJaExistenteException(
                    "Já existe uma nomeação para o processo "
                            + dadosAtualizados.getNumeroProcesso()
            );
        }

        nomeacaoPersistida.atualizarDados(
                dadosAtualizados
        );

        validarDadosEspecificos(nomeacaoPersistida);

        return nomeacaoRepository.save(
                nomeacaoPersistida
        );
    }

    @Override
    @Transactional(readOnly = true)
    public NomeacaoPericial obterPorId(Long id) {
        NomeacaoPericial nomeacao =
                super.obterPorId(id);

        inicializarRelacionamentos(nomeacao);
        return nomeacao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NomeacaoPericial> listarTodos() {
        List<NomeacaoPericial> nomeacoes =
                super.listarTodos();

        nomeacoes.forEach(this::inicializarRelacionamentos);

        return nomeacoes;
    }

    @Transactional(readOnly = true)
    public List<NomeacaoPericial> listarPorStatus(
            StatusNomeacao status
    ) {
        if (status == null) {
            throw new DadosInvalidosException(
                    "O status da nomeação é obrigatório"
            );
        }

        List<NomeacaoPericial> nomeacoes =
                nomeacaoRepository.findByStatus(status);

        nomeacoes.forEach(this::inicializarRelacionamentos);

        return nomeacoes;
    }

    @Transactional(readOnly = true)
    public List<NomeacaoPericial> listarOrdenadasPorPrazo() {
        List<NomeacaoPericial> nomeacoes =
                nomeacaoRepository
                        .findAllByOrderByDataLimiteAsc();

        nomeacoes.forEach(this::inicializarRelacionamentos);

        return nomeacoes;
    }

    @Transactional(readOnly = true)
    public NomeacaoPericial obterPorNumeroProcesso(
            String numeroProcesso
    ) {
        if (numeroProcesso == null
                || numeroProcesso.isBlank()) {
            throw new DadosInvalidosException(
                    "O número do processo é obrigatório"
            );
        }

        NomeacaoPericial nomeacao =
                nomeacaoRepository
                        .findByNumeroProcesso(numeroProcesso)
                        .orElseThrow(() ->
                                new EntidadeNaoEncontradaException(
                                        "Nomeação não encontrada para o processo: "
                                                + numeroProcesso
                                )
                        );

        inicializarRelacionamentos(nomeacao);
        return nomeacao;
    }

    @Transactional(readOnly = true)
    public List<String> listarNumerosProcessos() {
        return listarTodos()
                .stream()
                .map(NomeacaoPericial::getNumeroProcesso)
                .toList();
    }

    private void inicializarRelacionamentos(
            NomeacaoPericial nomeacao
    ) {
        nomeacao.getAtividades().size();
    }

    @Override
    protected void validarDadosEspecificos(
            NomeacaoPericial nomeacao
    ) {
        if (nomeacao == null) {
            throw new DadosInvalidosException(
                    "Os dados da nomeação são obrigatórios"
            );
        }

        if (nomeacao.getNumeroProcesso() == null
                || nomeacao.getNumeroProcesso().isBlank()) {
            throw new DadosInvalidosException(
                    "O número do processo é obrigatório"
            );
        }

        if (nomeacao.getDataNomeacao() == null) {
            throw new DadosInvalidosException(
                    "A data da nomeação é obrigatória"
            );
        }

        if (nomeacao.getPrazoEmDias() <= 0) {
            throw new DadosInvalidosException(
                    "O prazo da nomeação deve ser positivo"
            );
        }

        if (nomeacao.getStatus() == null) {
            throw new DadosInvalidosException(
                    "O status da nomeação é obrigatório"
            );
        }

        if (nomeacao.getHonorarios() == null) {
            throw new DadosInvalidosException(
                    "Os honorários da nomeação são obrigatórios"
            );
        }

        if (nomeacao.getPerito() == null) {
            throw new DadosInvalidosException(
                    "O perito da nomeação é obrigatório"
            );
        }
    }
}
