package com.gamzabat.algohub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {
	@Bean
	public OpenAPI openAPI(){
		return new OpenAPI()
			.info(new Info()
				.title("AlgoHub API 명세서")
				.description("AlgoHub API 명세서 입니다.")
				.version("1.0.0"));
	}
}
