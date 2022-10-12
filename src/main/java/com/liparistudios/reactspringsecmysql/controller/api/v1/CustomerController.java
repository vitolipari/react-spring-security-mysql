package com.liparistudios.reactspringsecmysql.controller.api.v1;

import com.liparistudios.reactspringsecmysql.model.Customer;
import com.liparistudios.reactspringsecmysql.model.Role;
import com.liparistudios.reactspringsecmysql.service.CustomerServiceImplementation;
import com.liparistudios.reactspringsecmysql.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.annotation.security.RolesAllowed;
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


    @GetMapping
    @ResponseBody
    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomer();
    }


    @GetMapping("/{page}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    public ResponseEntity<List<Customer>> getAllCustomers( @PathVariable Long page ) {
        return
            ResponseEntity
                .status(HttpStatus.OK)
                .body( customerService.getAllCustomer( page == null ? 0 : Math.toIntExact(page) ) )
        ;
    }


    @ResponseBody
    @GetMapping("/data")
    public Customer getCustomerData( HttpServletRequest request ) {
        Long id = ((Customer) request.getUserPrincipal()).getId();
        return customerService.loadCustomerById( id );
    }

    @ResponseBody
    @GetMapping("/data/full")
    @PreAuthorize("hasAnyRole('FREE_CUSTOMER')")
    public Customer getFullCustomerData( HttpServletRequest request ) {
        Long id = ((Customer) request.getUserPrincipal()).getId();
        return customerService.loadCustomerById( id );
    }


    @ResponseBody
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(
            HttpServletRequest request,
            @RequestBody Customer customer
    ) {

        System.out.println("\n");
        System.out.println( "ruolo admin?" );
        System.out.println( request.isUserInRole("ADMIN") );
        Principal principal = request.getUserPrincipal();
        System.out.println("Principal");
        System.out.println( principal );
        if( principal != null ) {
            System.out.println( principal.getName() );
            System.out.println(principal.getClass().getName());
        }
        System.out.println("Customer arrivato");
        System.out.println( customer );
        System.out.println( customer.toString() );
        System.out.println("\n");


        // controllo chi fa il signup
        if(request.isUserInRole("ADMIN")) {
            // some kind of legacy
        }
        else {

        }



        Customer savedCustomer = customerService.saveCustomer( customer );
        if( savedCustomer == null ) {
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

        System.out.println("Customer salvato");
        System.out.println( savedCustomer );
        System.out.println( savedCustomer.toMap() );


        return ResponseEntity.ok( savedCustomer.toMap() );
    }



}
