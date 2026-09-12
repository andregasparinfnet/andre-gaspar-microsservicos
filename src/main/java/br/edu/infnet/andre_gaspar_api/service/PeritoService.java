package br.edu.infnet.andre_gaspar_api.service;

import br.edu.infnet.andre_gaspar_api.exception.DadosInvalidosException;
import br.edu.infnet.andre_gaspar_api.exception.EntidadeJaExistenteException;
import br.edu.infnet.andre_gaspar_api.model.Perito;
import br.edu.infnet.andre_gaspar_api.repository.PeritoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PeritoService extends BaseCrudService<Perito> {

    private final PeritoRepository peritoRepository;

    public PeritoService(
            PeritoRepository peritoRepository
    ) {
        super(peritoRepository);
        this.peritoRepository = peritoRepository;
    }

    @Override
    @Transactional
    public Perito incluir(Perito perito) {
        validarDadosEspecificos(perito);

        if (peritoRepository.existsByEmail(
                perito.getEmail()
        )) {
            throw new EntidadeJaExistenteException(
                    "Já existe um perito com o e-mail "
                            + perito.getEmail()
            );
        }

        return super.incluir(perito);
    }

    @Override
    @Transactional
    public Perito alterar(Perito dadosAtualizados) {
        if (dadosAtualizados == null) {
            throw new DadosInvalidosException(
                    "Os dados do perito são obrigatórios"
            );
        }

        validarId(dadosAtualizados.getId());
        validarDadosEspecificos(dadosAtualizados);

        Perito peritoPersistido =
                obterPorId(dadosAtualizados.getId());

        if (peritoRepository.existsByEmailAndIdNot(
                dadosAtualizados.getEmail(),
                dadosAtualizados.getId()
        )) {
            throw new EntidadeJaExistenteException(
                    "Já existe um perito com o e-mail "
                            + dadosAtualizados.getEmail()
            );
        }

        peritoPersistido.atualizarDados(
                dadosAtualizados
        );

        return peritoRepository.save(peritoPersistido);
    }

    @Override
    @Transactional(readOnly = true)
    public Perito obterPorId(Long id) {
        Perito perito = super.obterPorId(id);
        inicializarRelacionamentos(perito);
        return perito;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Perito> listarTodos() {
        List<Perito> peritos = super.listarTodos();

        peritos.forEach(this::inicializarRelacionamentos);

        return peritos;
    }

    private void inicializarRelacionamentos(Perito perito) {
        perito.getNomeacoes().forEach(nomeacao ->
                nomeacao.getAtividades().size()
        );
    }

    @Override
    protected void validarDadosEspecificos(Perito perito) {
        if (perito == null) {
            throw new DadosInvalidosException(
                    "Os dados do perito são obrigatórios"
            );
        }

        if (perito.getNome() == null
                || perito.getNome().isBlank()) {
            throw new DadosInvalidosException(
                    "O nome do perito é obrigatório"
            );
        }

        if (perito.getEmail() == null
                || perito.getEmail().isBlank()) {
            throw new DadosInvalidosException(
                    "O e-mail do perito é obrigatório"
            );
        }
    }
}
