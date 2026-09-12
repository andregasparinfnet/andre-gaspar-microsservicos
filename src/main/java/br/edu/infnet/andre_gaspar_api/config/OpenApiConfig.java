package br.edu.infnet.andre_gaspar_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI configurarOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("André Gaspar API")
                        .version("0.4.0")
                        .description(
                                "API REST acadêmica para gerenciamento "
                                        + "de nomeações e atividades periciais. "
                                        + "Na Etapa 4, os dados são persistidos "
                                        + "em banco H2 com Spring Data JPA."
                        )
                );
    }
}