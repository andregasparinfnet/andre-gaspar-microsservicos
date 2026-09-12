package br.edu.infnet.andre_gaspar_api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "peritos")
public class Perito extends Pessoa {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @OneToMany(
            mappedBy = "perito",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<@Valid NomeacaoPericial> nomeacoes = new ArrayList<>();

    protected Perito() {
        super();
    }

    public Perito(Long id, String nome, String email) {
        super(id, nome, email);
    }

    public Perito(String nome, String email) {
        super(nome, email);
    }

    public List<NomeacaoPericial> getNomeacoes() {
        return Collections.unmodifiableList(nomeacoes);
    }

    public void adicionarNomeacao(NomeacaoPericial nomeacao) {
        if (nomeacao == null) {
            return;
        }

        nomeacoes.add(nomeacao);
        nomeacao.associarPerito(this);
    }

    public int quantidadeNomeacoes() {
        return nomeacoes.size();
    }

    public void atualizarDados(Perito dados) {
        atualizarDadosPessoais(
                dados.getNome(),
                dados.getEmail()
        );
    }

    @Override
    public String toString() {
        return "Perito{" +
                "id=" + getId() +
                ", nome='" + getNome() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", quantidadeNomeacoes=" + nomeacoes.size() +
                '}';
    }
}