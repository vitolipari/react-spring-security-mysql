package com.liparistudios.reactspringsecmysql;

import com.liparistudios.reactspringsecmysql.config.RsaKeyProperties;
import com.liparistudios.reactspringsecmysql.model.Customer;
import com.liparistudios.reactspringsecmysql.model.Permission;
import com.liparistudios.reactspringsecmysql.model.Role;
import com.liparistudios.reactspringsecmysql.service.CustomerService;
import com.liparistudios.reactspringsecmysql.service.PermissionService;
import com.liparistudios.reactspringsecmysql.service.RoleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
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
		PasswordEncoder passwordEncoder
	) {

		/*
		Si dovrebbe definire una classe annotata come Configuration
		per lanciare le query al commandLineRunner
		 */

		return (
			args -> {
				System.out.println("operazioni di avvio dell'app");

				// permessi --------------------------------------------------------------------------------
				Stream
					.of(
						new Permission(null, "LOGIN",               "permesso di accesso"),
						new Permission(null, "LOGOUT",              "permesso di uscita"),
						new Permission(null, "READ_CUSTOMER",       "permesso di lettura customers"),
						new Permission(null, "ADD_CUSTOMER",        "permesso di aggiunta customers"),
						new Permission(null, "EDIT_CUSTOMER",       "permesso di modifica customers"),
						new Permission(null, "DELETE_CUSTOMER",     "permesso di cancellazione customers"),
						new Permission(null, "DOWNLOAD_CUSTOMER",   "permesso di download dei dai del customers")
					)
					.forEach( perm -> {
						System.out.println("aggiungo permesso");
						System.out.println( perm.toString() );
						permissionService.addPermission(perm);
					})
				;

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

				System.out.println("fine dell'inserimento dei permessi");

			}
		);
	}


}
