package org.csbf.security.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;

@Configuration
//@EnableSwagger2
@ConditionalOnProperty(value = "springfox.documentation.enabled", havingValue = "true", matchIfMissing = true)
public class SwaggerConfig implements WebMvcConfigurer {

    @Bean
    public GroupedOpenApi ecomie() {
        return GroupedOpenApi.builder()
                .group("CSBF")
                .packagesToScan("org.csbf.security.controller")
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
                                .url("\"License URL\"")))
                .externalDocs(new ExternalDocumentation()
                        .description("\"External Documentation Description\"")
                        .url("\"External Documentation URL\"")

                )
                .components( new Components().addSecuritySchemes(
                        "api",
                        new SecurityScheme()
                                .scheme("bearer")
                                .type(SecurityScheme.Type.HTTP)
                                .bearerFormat("jwt") //if it is your case
                                .name("bearer")
                                .in(SecurityScheme.In.HEADER)
                ));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //enabling swagger-ui part for visual documentation
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }


}
