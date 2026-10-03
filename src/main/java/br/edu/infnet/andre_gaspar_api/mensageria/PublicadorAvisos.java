package br.edu.infnet.andre_gaspar_api.mensageria;

import br.edu.infnet.andre_gaspar_api.nomeacao.NomeacaoPericial;
import br.edu.infnet.andre_gaspar_api.nomeacao.NomeacaoPericialService;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PublicadorAvisos {

    private final NomeacaoPericialService nomeacoes;
    private final RabbitTemplate rabbitTemplate;

    public PublicadorAvisos(
            NomeacaoPericialService nomeacoes,
            RabbitTemplate rabbitTemplate
    ) {
        this.nomeacoes = nomeacoes;
        this.rabbitTemplate = rabbitTemplate;
    }

    public AvisoNomeacaoMessage publicar(Long nomeacaoId) {
        NomeacaoPericial nomeacao = nomeacoes.obterPorId(nomeacaoId);

        AvisoNomeacaoMessage aviso = new AvisoNomeacaoMessage(
                UUID.randomUUID(),
                nomeacao.getId(),
                nomeacao.getPeritoId(),
                nomeacao.getNumeroProcesso()
        );

        CorrelationData correlacao =
                new CorrelationData(aviso.avisoId().toString());

        try {
            rabbitTemplate.convertAndSend(
                    AvisoRabbitConfig.EXCHANGE,
                    AvisoRabbitConfig.ROTA,
                    aviso,
                    mensagem -> {
                        mensagem.getMessageProperties().setDeliveryMode(
                                MessageDeliveryMode.PERSISTENT
                        );
                        return mensagem;
                    },
                    correlacao
            );

            if (!correlacao.getFuture()
                    .get(5, TimeUnit.SECONDS)
                    .ack()
                    || correlacao.getReturned() != null) {
                throw new ResponseStatusException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "Broker não confirmou o aviso"
                );
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Publicação interrompida",
                    e
            );
        } catch (AmqpException | ExecutionException | TimeoutException e) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Broker indisponível para avisos",
                    e
            );
        }

        return aviso;
    }
}
