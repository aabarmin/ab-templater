package dev.abarmin.templater;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.abarmin.templater.config.Credentials;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(Credentials.class)
public class TemplaterApplication {

	public static void main(String[] args) {
		SpringApplication.run(TemplaterApplication.class, args);
	}

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper()
				.findAndRegisterModules();
	}
}
