package com.sneakyDateReforged.ms_notif.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI api() {
        return new OpenAPI().info(new Info()
                .title("ms-notif API")
                .version("v1")
                .description("Notifications in-app pour SneakyDateReforged"));
    }
}
