package com.eni.petrinet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PetriNetApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetriNetApplication.class, args);
	}

}
