package com.crypto.exchange.prices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PricesApplication {
	public static void main(String[] args) {
		SpringApplication.run(PricesApplication.class, args);
	}
}

