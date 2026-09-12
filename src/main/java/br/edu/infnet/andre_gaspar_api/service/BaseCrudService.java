package br.edu.infnet.andre_gaspar_api.service;

import br.edu.infnet.andre_gaspar_api.exception.DadosInvalidosException;
import br.edu.infnet.andre_gaspar_api.exception.EntidadeNaoEncontradaException;
import br.edu.infnet.andre_gaspar_api.model.Identificavel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public abstract class BaseCrudService<T extends Identificavel>
        implements CrudService<T, Long> {

    private final JpaRepository<T, Long> repository;

    protected BaseCrudService(
            JpaRepository<T, Long> repository
    ) {
        this.repository = repository;
    }

    @Override
    public T incluir(T objeto) {
        validarObjeto(objeto);

        if (objeto.getId() != null) {
            throw new DadosInvalidosException(
                    "O ID não deve ser informado na inclusão"
            );
        }

        return repository.save(objeto);
    }

    @Override
    public T alterar(T objeto) {
        validarObjeto(objeto);
        validarId(objeto.getId());

        if (!repository.existsById(objeto.getId())) {
            throw new EntidadeNaoEncontradaException(
                    "Entidade não encontrada: " + objeto.getId()
            );
        }

        return repository.save(objeto);
    }

    @Override
    public void excluir(Long id) {
        validarId(id);

        if (!repository.existsById(id)) {
            throw new EntidadeNaoEncontradaException(
                    "Entidade não encontrada: " + id
            );
        }

        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public T obterPorId(Long id) {
        validarId(id);

        return repository.findById(id)
                .orElseThrow(() ->
                        new EntidadeNaoEncontradaException(
                                "Entidade não encontrada: " + id
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> listarTodos() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public long contar() {
        return repository.count();
    }

    protected JpaRepository<T, Long> getRepository() {
        return repository;
    }

    protected void validarDadosEspecificos(T objeto) {
    }

    protected void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new DadosInvalidosException(
                    "O ID da entidade deve ser positivo"
            );
        }
    }

    private void validarObjeto(T objeto) {
        if (objeto == null) {
            throw new DadosInvalidosException(
                    "Os dados da entidade são obrigatórios"
            );
        }

        validarDadosEspecificos(objeto);
    }
}