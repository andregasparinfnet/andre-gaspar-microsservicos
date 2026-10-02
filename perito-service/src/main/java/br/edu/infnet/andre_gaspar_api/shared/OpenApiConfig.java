package br.edu.infnet.andre_gaspar_api.shared;

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
                        .title("Serviço de Peritos")
                        .version("0.4.0")
                        .description(
                                "API REST responsável pelo cadastro "
                                        + "e gerenciamento de peritos. "
                                        + "O serviço possui execução e "
                                        + "persistência independentes."
                        )
                );
    }
}