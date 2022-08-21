package dev.procheck.capweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import dev.procheck.capweb.dao.UserRepository;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
public class PKCapWeb extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(PKCapWeb.class, args);
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(PKCapWeb.class);
	}
}




