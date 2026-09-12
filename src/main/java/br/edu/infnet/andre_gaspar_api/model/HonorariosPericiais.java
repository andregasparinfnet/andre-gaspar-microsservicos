package br.edu.infnet.andre_gaspar_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

@Embeddable
public class HonorariosPericiais {

    @NotNull(message = "O valor proposto é obrigatório")
    @PositiveOrZero(message = "O valor proposto não pode ser negativo")
    @Column(
            name = "valor_proposto",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal valorProposto;

    @NotNull(message = "O valor fixado é obrigatório")
    @PositiveOrZero(message = "O valor fixado não pode ser negativo")
    @Column(
            name = "valor_fixado",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal valorFixado;

    @NotNull(message = "O valor recebido é obrigatório")
    @PositiveOrZero(message = "O valor recebido não pode ser negativo")
    @Column(
            name = "valor_recebido",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal valorRecebido;

    @Column(nullable = false)
    private boolean depositado;

    protected HonorariosPericiais() {
        this.valorProposto = BigDecimal.ZERO;
        this.valorFixado = BigDecimal.ZERO;
        this.valorRecebido = BigDecimal.ZERO;
        this.depositado = false;
    }

    public HonorariosPericiais(BigDecimal valorProposto) {
        this();
        this.valorProposto = valorProposto;
    }

    public BigDecimal getValorProposto() {
        return valorProposto;
    }

    public BigDecimal getValorFixado() {
        return valorFixado;
    }

    public BigDecimal getValorRecebido() {
        return valorRecebido;
    }

    public boolean isDepositado() {
        return depositado;
    }

    public void registrarValorFixado(BigDecimal valorFixado) {
        this.valorFixado = valorFixado;
    }

    public void registrarDeposito() {
        this.depositado = true;
    }

    public void registrarRecebimento(BigDecimal valorRecebido) {
        this.valorRecebido = valorRecebido;
    }

    @Override
    public String toString() {
        return "HonorariosPericiais{" +
                "valorProposto=" + valorProposto +
                ", valorFixado=" + valorFixado +
                ", valorRecebido=" + valorRecebido +
                ", depositado=" + depositado +
                '}';
    }
}