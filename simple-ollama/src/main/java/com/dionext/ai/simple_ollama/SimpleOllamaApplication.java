package com.dionext.ai.simple_ollama;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class SimpleOllamaApplication {

	public static void main(String[] args) {
		log.debug("1111");
		SpringApplication.run(SimpleOllamaApplication.class, args);
	}

}
