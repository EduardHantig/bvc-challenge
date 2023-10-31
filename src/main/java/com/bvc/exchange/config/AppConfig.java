package com.bvc.exchange.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public OpenAPI customOpenAPI() {
    String baseUrl = "https://bvc-exchange.com/api"; // Replace with your actual base URL

    return new OpenAPI()
        .info(new Info()
            .title("Exchange Service API")
            .version("1.0")
            .description("API for currency exchange operations. Base URL: " + baseUrl)
            .contact(new Contact().name("Eduard H").email("h.eduardgabor@gmail.com")));
  }
}
