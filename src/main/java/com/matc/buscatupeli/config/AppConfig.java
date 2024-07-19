package com.matc.buscatupeli.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    //para hacer llamadas HTTP a servicios RESTful (obtengo una lista de géneros desde la API TMDb)
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

