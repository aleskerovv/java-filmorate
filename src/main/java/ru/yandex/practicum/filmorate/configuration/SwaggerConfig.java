package ru.yandex.practicum.filmorate.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenApi(@Value("${application-description:Filmorate social-network project}") String appDescription,
                                 @Value("${application-version}") String appVersion) {
        return new OpenAPI().info(new Info().title("Filmorate")
                .version(appVersion)
                .description(appDescription));
    }
}
