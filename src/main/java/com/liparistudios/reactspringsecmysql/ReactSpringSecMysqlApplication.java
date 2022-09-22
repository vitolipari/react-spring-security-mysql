package com.liparistudios.reactspringsecmysql;

import com.liparistudios.reactspringsecmysql.service.PermissionService;
import com.liparistudios.reactspringsecmysql.service.RoleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ReactSpringSecMysqlApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactSpringSecMysqlApplication.class, args);
	}


	@Bean
	CommandLineRunner runner() {
		return (
			args -> {
				System.out.println("operazioni di avvio dell'app");

			}
		);
	}


}
