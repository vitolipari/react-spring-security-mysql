package com.liparistudios.reactspringsecmysql.config;

import com.liparistudios.reactspringsecmysql.model.permission.Permission;
import com.liparistudios.reactspringsecmysql.service.PermissionService;
import com.liparistudios.reactspringsecmysql.service.RoleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PermissionConfig {

    @Bean
    CommandLineRunner runner(PermissionService permissionService) {
        return (
                args -> {
                    System.out.println("operazioni di avvio al permission service");

                    List
                        .of(
                            new Permission(null, "LOGIN",               "permesso di accesso"),
                            new Permission(null, "LOGOUT",              "permesso di uscita"),
                            new Permission(null, "READ_CUSTOMER",       "permesso di lettura customers"),
                            new Permission(null, "ADD_CUSTOMER",        "permesso di aggiunta customers"),
                            new Permission(null, "EDIT_CUSTOMER",       "permesso di modifica customers"),
                            new Permission(null, "DELETE_CUSTOMER",     "permesso di cancellazione customers"),
                            new Permission(null, "DOWNLOAD_CUSTOMER",   "permesso di download dei dai del customers")
                        )
                        .stream()
                        .map( perm -> {
                            permissionService.addPermission(perm);
                            return null;
                        })
                    ;

                }
        );
    }

}
