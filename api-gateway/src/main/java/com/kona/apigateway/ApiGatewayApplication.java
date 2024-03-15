package com.kona.apigateway;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}
	@Bean
	public OpenAPI ApiGatewayOpenAPI() {
		return new OpenAPI()
				.info(new Info().title("api gateway")
						.description("All the apis of library management system")
						.version("1.0"));
	}

}
