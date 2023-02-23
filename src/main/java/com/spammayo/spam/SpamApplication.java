package com.spammayo.spam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpamApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpamApplication.class, args);
	}

}
