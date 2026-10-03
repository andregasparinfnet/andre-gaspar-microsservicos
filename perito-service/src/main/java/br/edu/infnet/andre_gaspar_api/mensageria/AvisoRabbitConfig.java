package br.edu.infnet.andre_gaspar_api.mensageria;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AvisoRabbitConfig {

    public static final String FILA = "avisos.nomeacoes";
    public static final String EXCHANGE = "pericias";
    public static final String ROTA = "nomeacao.aviso";

    @Bean
    Queue filaAvisos() {
        return new Queue(FILA, true);
    }

    @Bean
    DirectExchange exchangePericias() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    Binding bindingAvisos(
            Queue filaAvisos,
            DirectExchange exchangePericias
    ) {
        return BindingBuilder.bind(filaAvisos)
                .to(exchangePericias)
                .with(ROTA);
    }

    @Bean
    JacksonJsonMessageConverter conversorAvisos() {
        return new JacksonJsonMessageConverter(
                "br.edu.infnet.andre_gaspar_api.mensageria"
        );
    }
}
