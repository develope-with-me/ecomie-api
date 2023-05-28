package org.csbf.security.config;

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
                ));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //enabling swagger-ui part for visual documentation
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }


 /*   @Bean
    public Docket api() {
//        return new Docket(DocumentationType.OAS_30)
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
//                .apis(RequestHandlerSelectors.any())
//                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .apis(RequestHandlerSelectors.basePackage("org.csbf.security.controller"))
                .paths(PathSelectors.any())
                .build()
                .forCodeGeneration(true);
    }
*/
  /*  private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("CSBF-SECURITY-API")
                .description("An api for authenticating users")
                .contact(new Contact("Domou Brice", "https://www.linkedin.com/in/domoubrice", "domoubrice@gmail.com"))
                .version("1.0.0")
                .build();
    }
*/

//    @Override
//    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("swagger-ui.html")
//                .addResourceLocations("classpath:/META-INF/resources/");
//        registry.addResourceHandler("/webjars/**")
//                .addResourceLocations("classpath:/META-INF/resources/webjars/");
//    }

//    @Bean
//    public Docket api() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .select()
//                .apis(RequestHandlerSelectors.any())
//                .paths(PathSelectors.any())
//                .build();
//    }
}
