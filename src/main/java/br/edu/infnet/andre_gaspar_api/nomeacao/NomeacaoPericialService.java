package br.edu.infnet.andre_gaspar_api.nomeacao;
import br.edu.infnet.andre_gaspar_api.shared.BaseCrudService;
import br.edu.infnet.andre_gaspar_api.nomeacao.dto.NomeacaoResumoResponse;

import br.edu.infnet.andre_gaspar_api.nomeacao.StatusNomeacao;
import br.edu.infnet.andre_gaspar_api.shared.DadosInvalidosException;
import br.edu.infnet.andre_gaspar_api.shared.EntidadeJaExistenteException;
import br.edu.infnet.andre_gaspar_api.shared.EntidadeNaoEncontradaException;
import br.edu.infnet.andre_gaspar_api.nomeacao.NomeacaoPericial;
import br.edu.infnet.andre_gaspar_api.nomeacao.NomeacaoPericialRepository;
import br.edu.infnet.andre_gaspar_api.nomeacao.client.PeritoClient;
import br.edu.infnet.andre_gaspar_api.nomeacao.dto.PeritoResumoResponse;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NomeacaoPericialService
        extends BaseCrudService<NomeacaoPericial> {

    private final NomeacaoPericialRepository nomeacaoRepository;
    private final PeritoClient peritoClient;

    public NomeacaoPericialService(
            NomeacaoPericialRepository nomeacaoRepository,
            PeritoClient peritoClient
    ) {
        super(nomeacaoRepository);
        this.nomeacaoRepository = nomeacaoRepository;
        this.peritoClient = peritoClient;
    }

    @Transactional(readOnly = true)
    public List<NomeacaoResumoResponse> listarResumos() {
        return listarTodos()
                .stream()
                .map(NomeacaoResumoResponse::de)
                .toList();
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
        validarPeritoRemoto(peritoId);
        nomeacao.setPeritoId(peritoId);

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

        Long peritoIdEfetivo = dadosAtualizados.getPeritoId() == null
                ? nomeacaoPersistida.getPeritoId()
                : dadosAtualizados.getPeritoId();

        validarPeritoRemoto(peritoIdEfetivo);
        dadosAtualizados.setPeritoId(peritoIdEfetivo);

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

    private void validarPeritoRemoto(Long peritoId) {
        if (peritoId == null) {
            throw new DadosInvalidosException(
                    "O identificador do perito é obrigatório"
            );
        }

        try {
            PeritoResumoResponse perito =
                    peritoClient.obterPorId(peritoId);

            if (perito == null || perito.id() == null) {
                throw new ResponseStatusException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "Serviço de peritos temporariamente indisponível"
                );
            }
        } catch (FeignException.NotFound excecao) {
            throw new EntidadeNaoEncontradaException(
                    "Perito não encontrado: " + peritoId
            );
        } catch (FeignException excecao) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Serviço de peritos temporariamente indisponível"
            );
        }
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

        if (nomeacao.getPeritoId() == null) {
            throw new DadosInvalidosException(
                    "O identificador do perito é obrigatório"
            );
        }
    }
}
