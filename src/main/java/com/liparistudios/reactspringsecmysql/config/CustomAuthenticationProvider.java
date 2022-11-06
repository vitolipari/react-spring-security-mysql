package com.liparistudios.reactspringsecmysql.config;

import com.liparistudios.reactspringsecmysql.model.Customer;
import com.liparistudios.reactspringsecmysql.service.CustomerServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {


    @Autowired
    private CustomEncoder encoder;

    @Autowired
    private CustomerServiceImplementation customerService;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        System.out.println("CustomAuthenticationProvider");
        System.out.println("authentication, name, principal, credentials");
        System.out.println(authentication);
        System.out.println(authentication.getName());
        System.out.println(authentication.getPrincipal());
        System.out.println(authentication.getCredentials());


        String passwordEncoded = encoder.passwordEncoder().encode( authentication.getCredentials().toString() );
//        System.out.println("passwordEncoded");
//        System.out.println(passwordEncoded);


        Customer customer = customerService.loadCustomerByUsername( authentication.getName() );
        System.out.println("customer");
        System.out.println(customer);
        System.out.println("le password corrispondono?");
        System.out.println( customer.getPassword().equals( passwordEncoded ) );
        System.out.println( customer.getPassword() );
        System.out.println( passwordEncoded );


        if ( customer.getPassword().equals( passwordEncoded ) ) {
            System.out.println("Auth Success");


            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken( customer.getUsername(), customer.getPassword(), customer.getAuthorities() );
            System.out.println("auth token, credentials, principal, name, authorities, details, isAuthenticated");
            System.out.println(authToken);
            System.out.println(authToken.getCredentials());
            System.out.println(authToken.getPrincipal());
            System.out.println(authToken.getName());
            System.out.println(authToken.getAuthorities());
            System.out.println(authToken.getDetails());
            System.out.println(authToken.isAuthenticated());


            // da capire se avviene tutte le volte o soltanto al login
            // nel caso di login
            // jwt
            // session

            return authToken;

        } else {
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
