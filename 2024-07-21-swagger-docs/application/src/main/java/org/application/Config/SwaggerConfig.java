package org.application.Config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * localhost:9000/swagger-ui/index.html/
 */
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }



    private Info apiInfo(){
        return new Info()
                .title("api-server docs")
                .description("api docs입니다.")
                .version("1.0.0");
    }
}