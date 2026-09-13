package br.edu.infnet.andre_gaspar_api.perito;

import br.edu.infnet.andre_gaspar_api.shared.DadosInvalidosException;
import br.edu.infnet.andre_gaspar_api.shared.EntidadeJaExistenteException;
import br.edu.infnet.andre_gaspar_api.shared.BaseCrudService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
