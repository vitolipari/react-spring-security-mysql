package com.liparistudios.reactspringsecmysql.controller.api.v1;

import com.liparistudios.reactspringsecmysql.model.Customer;
import com.liparistudios.reactspringsecmysql.model.Role;
import com.liparistudios.reactspringsecmysql.service.CustomerServiceImplementation;
import com.liparistudios.reactspringsecmysql.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/customer")
public class CustomerController {

    @Autowired
    private CustomerServiceImplementation customerService;

    @Autowired
    private RoleService roleService;

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/customer").toUriString());
        return ResponseEntity.created( uri ).body( customerService.saveCustomer( customer ) );
    }

    @PostMapping("/signup")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> signUp(
            HttpServletRequest request,
            @RequestBody(required = true) String email,
            @RequestBody(required = true) String hashedPassword,
            @RequestBody(required = false) List<String> rolesName
    ) {

        Role role = null;

        // controllo chi fa il signup
        if(request.isUserInRole("ADMIN")) {
            // some kind of legacy
        }
        else {

        }

        try {

            customerService.loadCustomerByEmail( email );
            // email esistente

            return
                ResponseEntity
                    .status( HttpStatus.INTERNAL_SERVER_ERROR )
                    .body(
                        new HashMap<String, Object>(){{
                            put("error", new HashMap<String, Object>(){{
                                put("message", "Email Esistente");
                            }});
                        }}
                    )
                ;

        }
        catch (Exception e) {
            // non esiste la email passata

        }


        return
            ResponseEntity.ok(
                customerService.saveCustomer(
                    new Customer(
                        null,
                        email,
                        hashedPassword,
                        roleService.findAll()
                            .stream()
                            .filter( currentRole -> rolesName.contains( currentRole.getName() ) )
                            .collect(Collectors.toList())
                    )
                )
                .toMap()
            )
        ;


    }

}
