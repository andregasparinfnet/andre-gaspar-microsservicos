package br.edu.infnet.andre_gaspar_api.model;

import br.edu.infnet.andre_gaspar_api.enums.StatusNomeacao;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "nomeacoes_periciais")
public class NomeacaoPericial implements Identificavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O número do processo é obrigatório")
    @Size(
            max = 40,
            message = "O número do processo deve possuir no máximo 40 caracteres"
    )
    @Column(
            name = "numero_processo",
            nullable = false,
            unique = true,
            length = 40
    )
    private String numeroProcesso;

    @NotNull(message = "A data da nomeação é obrigatória")
    @Column(name = "data_nomeacao", nullable = false)
    private LocalDate dataNomeacao;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "data_limite", nullable = false)
    private LocalDate dataLimite;

    @Min(
            value = 1,
            message = "O prazo da nomeação deve ser de pelo menos um dia"
    )
    @Column(name = "prazo_em_dias", nullable = false)
    private int prazoEmDias;

    @NotNull(message = "O status da nomeação é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusNomeacao status;

    @Valid
    @NotNull(message = "Os honorários da nomeação são obrigatórios")
    @Embedded
    private HonorariosPericiais honorarios;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "perito_id", nullable = false)
    private Perito perito;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @OneToMany(
            mappedBy = "nomeacao",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<@Valid AtividadePericial> atividades = new ArrayList<>();

    protected NomeacaoPericial() {
        this.status = StatusNomeacao.RECEBIDA;
    }

    public NomeacaoPericial(
            Long id,
            String numeroProcesso,
            LocalDate dataNomeacao,
            int prazoEmDias,
            HonorariosPericiais honorarios
    ) {
        this.id = id;
        this.numeroProcesso = numeroProcesso;
        this.dataNomeacao = dataNomeacao;
        this.prazoEmDias = prazoEmDias;
        this.dataLimite = dataNomeacao.plusDays(prazoEmDias);
        this.honorarios = honorarios;
        this.status = StatusNomeacao.RECEBIDA;
    }

    public NomeacaoPericial(
            String numeroProcesso,
            LocalDate dataNomeacao,
            int prazoEmDias,
            HonorariosPericiais honorarios
    ) {
        this(
                null,
                numeroProcesso,
                dataNomeacao,
                prazoEmDias,
                honorarios
        );
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public LocalDate getDataNomeacao() {
        return dataNomeacao;
    }

    public LocalDate getDataLimite() {
        return dataLimite;
    }

    public int getPrazoEmDias() {
        return prazoEmDias;
    }

    public StatusNomeacao getStatus() {
        return status;
    }

    public HonorariosPericiais getHonorarios() {
        return honorarios;
    }

    public List<AtividadePericial> getAtividades() {
        return Collections.unmodifiableList(atividades);
    }

    public Perito getPerito() {
        return perito;
    }

    public void associarPerito(Perito perito) {
        this.perito = perito;
    }

    public void atualizarDados(NomeacaoPericial dados) {
        this.numeroProcesso = dados.numeroProcesso;
        this.dataNomeacao = dados.dataNomeacao;
        this.prazoEmDias = dados.prazoEmDias;
        this.status = dados.status;
        this.honorarios = dados.honorarios;
        recalcularDataLimite();
    }

    public void recalcularDataLimite() {
        if (dataNomeacao != null && prazoEmDias > 0) {
            dataLimite = dataNomeacao.plusDays(prazoEmDias);
        }
    }

    @PrePersist
    @PreUpdate
    private void prepararPersistencia() {
        recalcularDataLimite();
    }

    public void aceitar() {
        this.status = StatusNomeacao.ACEITA;
    }

    public void recusar() {
        this.status = StatusNomeacao.RECUSADA;
    }

    public void alterarStatus(StatusNomeacao novoStatus) {
        this.status = novoStatus;
    }

    public void adicionarAtividade(AtividadePericial atividade) {
        if (atividade == null) {
            return;
        }

        atividades.add(atividade);
        atividade.associarNomeacao(this);
    }

    public int quantidadeAtividades() {
        return atividades.size();
    }

    public boolean estaAtrasada() {
        return status != StatusNomeacao.FINALIZADA
                && dataLimite.isBefore(LocalDate.now());
    }

    @Override
    public String toString() {
        return "NomeacaoPericial{" +
                "id=" + id +
                ", numeroProcesso='" + numeroProcesso + '\'' +
                ", dataNomeacao=" + dataNomeacao +
                ", dataLimite=" + dataLimite +
                ", prazoEmDias=" + prazoEmDias +
                ", status=" + status +
                ", honorarios=" + honorarios +
                ", quantidadeAtividades=" + atividades.size() +
                '}';
    }
}