package br.com.devdojo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;

@SpringBootApplication
public class ExamGenerationApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExamGenerationApplication.class, args);
	}

	//metodo para corrigir erro quando utilizado EL Spring para buscar id curso com professor
	@Bean
	public SecurityEvaluationContextExtension securityEvaluationContextExtension(){
		return new SecurityEvaluationContextExtension();
	}

}
