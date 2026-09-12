package br.edu.infnet.andre_gaspar_api.service;

import java.util.List;

public interface CrudService<T, ID> {

    T incluir(T objeto);

    T alterar(T objeto);

    void excluir(ID id);

    T obterPorId(ID id);

    List<T> listarTodos();
}

