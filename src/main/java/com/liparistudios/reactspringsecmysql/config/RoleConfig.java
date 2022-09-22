package com.liparistudios.reactspringsecmysql.config;

import com.liparistudios.reactspringsecmysql.model.permission.Permission;
import com.liparistudios.reactspringsecmysql.model.role.Role;
import com.liparistudios.reactspringsecmysql.service.PermissionService;
import com.liparistudios.reactspringsecmysql.service.RoleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RoleConfig {

    @Bean
    CommandLineRunner runner(RoleService roleService, PermissionService permissionService) {
        return (
            args -> {
                System.out.println("operazioni di avvio al role service");
                List
                    .of(
                        new Role(
                                null,
                                "USER",
                                new ArrayList<Permission>(){{
                                    add( permissionService.getByName("LOGIN").get() );
                                }}
                        ),
                        new Role(null, "ADMIN", permissionService.getAllPermissions())
                    )
                    .stream()
                    .map( role -> {
                        roleService.addRole( role );
                        return null;
                    })
                ;
            }
        );
    }

}
