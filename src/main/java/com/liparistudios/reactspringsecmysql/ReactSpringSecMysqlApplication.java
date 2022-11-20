package com.liparistudios.reactspringsecmysql;

import com.liparistudios.reactspringsecmysql.config.RsaKeyProperties;
import com.liparistudios.reactspringsecmysql.model.*;
import com.liparistudios.reactspringsecmysql.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

//@EnableConfigurationProperties(RsaKeyProperties.class)
@SpringBootApplication
public class ReactSpringSecMysqlApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactSpringSecMysqlApplication.class, args);
	}

	// @Bean
	// PasswordEncoder passwordEncoder() {
		// return new BCryptPasswordEncoder();
	//}

	@Bean
	CommandLineRunner runner(
		PermissionService permissionService,
		RoleService roleService,
		CustomerService customerService,
		PlatformService platformService,
		PasswordEncoder passwordEncoder,
		SessionService sessionService
	) {

		/*
		Si dovrebbe definire una classe annotata come Configuration
		per lanciare le query al commandLineRunner
		 */

		return (
			args -> {
				System.out.println("operazioni di avvio dell'app");


				// permessi --------------------------------------------------------------------------------
				// aggiunti tramite file data.sql

				// ruoli --------------------------------------------------------------------------------
				Stream
					.of(
						new Role(
							null,
							"ADMIN",
							permissionService.getAllPermissions()
						),
						new Role(
							null,
							"FREE_CUSTOMER",
							new ArrayList<Permission>(){{
								add( permissionService.getByName("LOGIN") );
								add( permissionService.getByName("LOGOUT") );
							}}
						),
							new Role(
								null,
								"MASTER",
								permissionService.getAllPermissions()
							)
					)
					.forEach( role -> {
						System.out.println("aggiungo ruolo");
						System.out.println( role.toString() );
						roleService.addRole( role );
					})
				;


				// customer --------------------------------------------------------------------------------
				System.out.println("preparo il customer");
				customerService.saveCustomer(
					new Customer(
						null,
						"vitolipari1981@gmail.com",
						passwordEncoder.encode("abc"),
						"+393000000000",
						LocalDate.of(1981, 4, 25),
						new ArrayList<Role>(){{
							add( roleService.findByName("MASTER") );
						}}
					)
				);

				customerService.saveCustomer(
					new Customer(
						null,
						"test42@mail.com",
						passwordEncoder.encode("abc"),
						"+393000000000",
						LocalDate.of(1981, 4, 25),
						new ArrayList<Role>(){{
							add( roleService.findByName("FREE_CUSTOMER") );
						}}
					)
				);

				System.out.println("fine dell'inserimento dei permessi");


				Platform mobileAgentDiagnosticPortal =
					new Platform(
						null,
						"Mobile Agent Dignostic Portal",
						null,
						LocalDateTime.now(),
						null,
						null,
						null,
						null,
						null
					)
				;
//				platformService.addPlatform( mobileAgentDiagnosticPortal );
//				System.out.println("Piattaforma aggiunta");


				Session firstSession =
					new Session(
						null,
						"e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
						LocalDateTime.now(),
						null,
						null,
						LocalDateTime.now().plus(90, ChronoUnit.DAYS),
						null,
						null
					)
				;
				sessionService.save( firstSession );
				System.out.println("salvata la sessione");
				List<Session> platformSessions = mobileAgentDiagnosticPortal.getSessions();
				platformSessions.add( firstSession );
				System.out.println("aggiunta la nuova sessione alla lista delle sessioni");
				mobileAgentDiagnosticPortal.setSessions( platformSessions );
				System.out.println("lista sessioni impostata");
				platformService.save( mobileAgentDiagnosticPortal );
				System.out.println("piattaforma salvata");

				System.out.println("Nuova sessione creata");

			}
		);
	}


}
