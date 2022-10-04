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
import java.security.Principal;
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



    @ResponseBody
    @PostMapping("/signup")
    public ResponseEntity<Customer> signup(
            HttpServletRequest request,
            @RequestBody Customer customer
    ) {

        System.out.println("\n");
        System.out.println( "ruolo admin?" );
        System.out.println( request.isUserInRole("ADMIN") );
        Principal principal = request.getUserPrincipal();
        System.out.println("Principal");
        System.out.println( principal );
        //System.out.println( principal.getName() );
        //System.out.println( principal.getName().toString() );
        System.out.println("Customer arrivato");
        System.out.println( customer );
        System.out.println( customer.toString() );
        System.out.println( customer.toMap() );
        System.out.println( customer.toMap().toString() );
        System.out.println("\n");

        return ResponseEntity.ok( customer );
    }

    @ResponseBody
    @PostMapping("/signup/parameters")
    public ResponseEntity<Map<String, Object>> signUp(

            HttpServletRequest request,
            @RequestParam(name = "email", required = true) String email,
            @RequestParam(name = "hashedPassword", required = true) String password,
            @RequestParam(name = "rolesName", required = false) List<String> roles

    ) {



        System.out.println("\n");
        System.out.println( "ruolo admin?" );
        System.out.println( request.isUserInRole("ADMIN") );
        Principal principal = request.getUserPrincipal();
        System.out.println("Principal");
        System.out.println( principal );
        System.out.println( principal.getName() );
        System.out.println( principal.getName().toString() );
        System.out.println("\n");




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
                        password,
                        roleService.findAll()
                            .stream()
                            .filter( currentRole -> roles.contains( currentRole.getName() ) )
                            .collect(Collectors.toList())
                    )
                )
                .toMap()
            )
        ;


    }

}
