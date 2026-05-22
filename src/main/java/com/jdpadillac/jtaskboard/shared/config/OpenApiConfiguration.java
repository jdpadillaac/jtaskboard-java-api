package com.jdpadillac.jtaskboard.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI jtaskboardOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("JTaskboard API")
                        .description("Documentacion OpenAPI para gestionar tareas")
                        .version("v1")
                        .contact(new Contact().name("jtaskboard")));
    }
}

