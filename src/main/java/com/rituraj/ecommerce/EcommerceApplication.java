package com.rituraj.ecommerce;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.io.File;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.rituraj.ecommerce")

public class EcommerceApplication {

	@PostConstruct
	public void init() {
		// Create logs directory if it doesn't exist
		File logsDir = new File("/home/crest/Documents/var/logs/ecommerce");
		if (!logsDir.exists()) {
			logsDir.mkdirs();
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(EcommerceApplication.class, args);
	}
}
