package com.liparistudios.reactspringsecmysql.config;

import com.liparistudios.reactspringsecmysql.model.Permission;
import com.liparistudios.reactspringsecmysql.service.PermissionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Stream;

@Configuration
public class PermissionConfig {

    @Bean
    CommandLineRunner permissionCommandLineRunner(PermissionService permissionService) {
        return (
                args -> {
                    System.out.println("operazioni di avvio al permission service");

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

                    System.out.println("fine dell'inserimento dei permessi");

                }
        );
    }

}
