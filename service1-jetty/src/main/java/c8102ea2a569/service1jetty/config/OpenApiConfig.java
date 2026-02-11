package c8102ea2a569.service1jetty.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Music Band Management API")
                        .version("1.0")
                        .description("Service 1 - Music Band Management (Jetty)"))
                .addServersItem(new Server().url("https://localhost:8443").description("Service 1 Server"));
    }
}