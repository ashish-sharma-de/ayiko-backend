package com.ayiko.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories
@EnableJpaAuditing
@EnableScheduling
public class AyikoBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AyikoBackendApplication.class, args);
	}

}
