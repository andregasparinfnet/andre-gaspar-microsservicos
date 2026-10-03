package br.edu.infnet.andre_gaspar_api.mensageria;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ConsumidorAvisos {

    private final AvisoRecebidoRepository avisos;

    public ConsumidorAvisos(AvisoRecebidoRepository avisos) {
        this.avisos = avisos;
    }

    @RabbitListener(queues = AvisoRabbitConfig.FILA)
    @Transactional
    public void receber(AvisoNomeacaoMessage aviso) {
        if (aviso.avisoId() == null || aviso.nomeacaoId() == null
                || aviso.peritoId() == null || aviso.numeroProcesso() == null) {
            throw new IllegalArgumentException("Aviso de nomeação incompleto");
        }

        if (!avisos.existsByAvisoId(aviso.avisoId().toString())) {
            avisos.save(new AvisoRecebido(aviso));
        }
    }
}
