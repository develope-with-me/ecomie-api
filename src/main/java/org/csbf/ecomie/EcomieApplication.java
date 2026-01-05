package org.csbf.ecomie;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.csbf.ecomie.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@Slf4j
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class EcomieApplication implements CommandLineRunner {

	@Autowired
	private Environment env;

	@Resource
	FileUploadService fileUploadService;

	public static void main(String[] args) {
		log.info("Setting up corsConfig and file storage init Config {}", EcomieApplication.class);
		SpringApplication.run(EcomieApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		log.info("Setting up corsConfig: ApiSecurityApplication.corsConfigurer");
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedHeaders("*")
						.allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH", "OPTIONS")
						.allowedOrigins(env.getProperty("cors.allowed-origins.local-one"),env.getProperty("cors.allowed-origins.local-two"),env.getProperty("cors.allowed-origins.dev"),env.getProperty("cors.allowed-origins.prod"))
						.allowCredentials(true);
//						we can also set allowed Origins, OriginPatterns, Methods, Headers and Credentials as well as maxAge and exposedHeaders
			}
		};
	}

	@Override
	public void run(String... arg) throws Exception {
		log.info("Setting up file upload {}", EcomieApplication.class);
		fileUploadService.init();
	}
}
