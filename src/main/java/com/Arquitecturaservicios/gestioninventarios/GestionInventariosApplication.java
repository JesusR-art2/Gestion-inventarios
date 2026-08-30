package com.Arquitecturaservicios.gestioninventarios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.Arquitecturaservicios.gestioninventarios.Entities")
public class GestionInventariosApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionInventariosApplication.class, args);
	}

}
