package com.matc.buscatupeli;

import com.matc.buscatupeli.dto.UserDto;
import com.matc.buscatupeli.models.User;
import com.matc.buscatupeli.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BuscatupeliApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuscatupeliApplication.class, args);
	}

}
