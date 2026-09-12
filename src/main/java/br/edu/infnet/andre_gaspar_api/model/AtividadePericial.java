package br.edu.infnet.andre_gaspar_api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
@Table(name = "atividades_periciais")
public class AtividadePericial implements Identificavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "A descrição da atividade é obrigatória")
    @Size(
            min = 3,
            max = 200,
            message = "A descrição deve possuir entre 3 e 200 caracteres"
    )
    @Column(nullable = false, length = 200)
    private String descricao;

    @NotNull(message = "O prazo da atividade é obrigatório")
    @Column(nullable = false)
    private LocalDate prazo;

    @Positive(message = "As horas estimadas devem ser positivas")
    @Column(name = "horas_estimadas", nullable = false)
    private double horasEstimadas;

    @Column(nullable = false)
    private boolean concluida;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "nomeacao_id", nullable = false)
    private NomeacaoPericial nomeacao;

    protected AtividadePericial() {
    }

    public AtividadePericial(
            Long id,
            String descricao,
            LocalDate prazo,
            double horasEstimadas
    ) {
        this.id = id;
        this.descricao = descricao;
        this.prazo = prazo;
        this.horasEstimadas = horasEstimadas;
        this.concluida = false;
    }

    public AtividadePericial(
            String descricao,
            LocalDate prazo,
            double horasEstimadas
    ) {
        this(null, descricao, prazo, horasEstimadas);
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public LocalDate getPrazo() {
        return prazo;
    }

    public double getHorasEstimadas() {
        return horasEstimadas;
    }

    public boolean isConcluida() {
        return concluida;
    }

    public NomeacaoPericial getNomeacao() {
        return nomeacao;
    }

    public void associarNomeacao(NomeacaoPericial nomeacao) {
        this.nomeacao = nomeacao;
    }

    public void atualizarDados(AtividadePericial dados) {
        this.descricao = dados.descricao;
        this.prazo = dados.prazo;
        this.horasEstimadas = dados.horasEstimadas;
        this.concluida = dados.concluida;
    }

    public void concluir() {
        this.concluida = true;
    }

    public void reabrir() {
        this.concluida = false;
    }

    public boolean estaAtrasada() {
        return !concluida && prazo.isBefore(LocalDate.now());
    }

    @Override
    public String toString() {
        return "AtividadePericial{" +
                "id=" + id +
                ", descricao='" + descricao + '\'' +
                ", prazo=" + prazo +
                ", horasEstimadas=" + horasEstimadas +
                ", concluida=" + concluida +
                '}';
    }
}