package org.csbf.ecomie.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@Configuration
//@EnableSwagger2
@ConditionalOnProperty(value = "springfox.documentation.enabled", havingValue = "true", matchIfMissing = true)
public class SwaggerConfig implements WebMvcConfigurer {

    private final Environment environment;

    public SwaggerConfig(Environment environment) {
        this.environment = environment;
    }


    @Bean
    public GroupedOpenApi ecomie() {
        return GroupedOpenApi.builder()
                .group("CSBF")
                .packagesToScan("org.csbf.ecomie.controller")
                .pathsToMatch("/**").build();
    }

    @Bean
    public OpenAPI ecomieAPI() {
        return new OpenAPI()

                .info(new Info()
                        .title("CSBF-SECURITY-API")
                        .description("An api for authenticating users")
                        .version("\"version\"")
                        .contact(new Contact().name("Domou Brice").email("domoubrice@gmail.com").url("https://www.linkedin.com/in/domoubrice"))
                        .license(new License()
                                .name("\"License Name\"")
                                .url("\"License URL\"")
                        )
                )
                .servers(List.of(
                        new Server().url(environment.getProperty("LOCAL_SERVER")).description("Local Server"),
                        new Server().url(environment.getProperty("DEV_SERVER")).description("Development Server"),
                        new Server().url(environment.getProperty("PROD_SERVER")).description("Production Server")
                        )
                )
                .externalDocs(new ExternalDocumentation()
                        .description("\"External Documentation Description\"")
                        .url("\"External Documentation URL\"")
                )
                .components( new Components()
                        .addSecuritySchemes(
                                "bearerAuth",
                                new SecurityScheme()
                                    .scheme("bearer")
                                    .type(SecurityScheme.Type.HTTP)
                                    .bearerFormat("jwt") //if it is your case
                                    .name("bearer")
                                    .in(SecurityScheme.In.HEADER)

                )
                        .addSecuritySchemes(
                            "ApiKeyAuth",
                                new SecurityScheme()
                                    .scheme("ApiKey")
                                    .type(SecurityScheme.Type.APIKEY)
                                    .name("X-API-Key")
                                    .in(SecurityScheme.In.HEADER)

                )
        );
//        "securitySchemes":{"bearerAuth":{"type":"http","scheme":"bearer","bearerFormat":"JWT"},"ApiKeyAuth":{"type":"apiKey","in":"header","name":"ApiKey"}}
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //enabling swagger-ui part for visual documentation
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }


}
