package com.tp1.proyecto.configuracion;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracionOpenApi {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Rendimiento Academico Backend")
                .version("1.0.0")
                .description("API del sistema de analisis de rendimiento escolar")
                .contact(new Contact().name("Proyecto TP1")));
    }
}
