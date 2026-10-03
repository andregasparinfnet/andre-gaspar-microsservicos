package br.edu.infnet.andre_gaspar_api.mensageria;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "avisos_recebidos",
        uniqueConstraints = @UniqueConstraint(columnNames = "aviso_id")
)
public class AvisoRecebido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aviso_id", nullable = false, length = 36)
    private String avisoId;

    @Column(name = "nomeacao_id", nullable = false)
    private Long nomeacaoId;

    @Column(name = "perito_id", nullable = false)
    private Long peritoId;

    @Column(name = "numero_processo", nullable = false, length = 100)
    private String numeroProcesso;

    protected AvisoRecebido() {
    }

    public AvisoRecebido(AvisoNomeacaoMessage aviso) {
        this.avisoId = aviso.avisoId().toString();
        this.nomeacaoId = aviso.nomeacaoId();
        this.peritoId = aviso.peritoId();
        this.numeroProcesso = aviso.numeroProcesso();
    }

    public Long getId() { return id; }
    public String getAvisoId() { return avisoId; }
    public Long getNomeacaoId() { return nomeacaoId; }
    public Long getPeritoId() { return peritoId; }
    public String getNumeroProcesso() { return numeroProcesso; }
}
