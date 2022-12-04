package com.liparistudios.reactspringsecmysql.controller.api.v1;

import com.liparistudios.reactspringsecmysql.config.JwtTokenUtil;
import com.liparistudios.reactspringsecmysql.model.Customer;
import com.liparistudios.reactspringsecmysql.model.LoginAuthPack;
import com.liparistudios.reactspringsecmysql.service.CustomerServiceImplementation;
//import com.liparistudios.reactspringsecmysql.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {



    @Autowired
    private CustomerServiceImplementation customerService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // @Autowired
    // AuthenticationManager authManager;

    @Autowired
    JwtTokenUtil jwtUtil;


    /*
    //@PostMapping("/token")
    @GetMapping("/token")
    public String token(Authentication authentication) {
        String token = tokenService.generateToken( authentication );
        System.out.println("Token granted for user "+ authentication.getName());
        System.out.println( token );
        return token;
    }
*/

    @PostMapping("/public/login")
    public ResponseEntity<Map<String, Object>> login(
            HttpServletRequest request,
            @RequestBody LoginAuthPack credentials
            // @RequestParam String email,
            // @RequestParam String password
    ) {
        System.out.println("POST al login");
        System.out.println( "AUTH type" );
        System.out.println( request.getAuthType() );
        System.out.println("principal");
        System.out.println( request.getUserPrincipal() );

        System.out.println("email arrivata");
        System.out.println( credentials.getEmail() );
        System.out.println();

        Map<String, Object> result = null;
        try {




            Customer customer =
                    customerService
                            .loadCustomerByEmail( credentials.getEmail() )
            ;

            if( customer.getPassword().equals( passwordEncoder.encode( credentials.getPassword() ) ) ) {
                // login OK
                result = new HashMap<String, Object>(){{
                    put("status", "success");
                }};
            }
            else {
                // password errata
                result = new HashMap<String, Object>(){{
                    put("status", "error");
                    put("error", new HashMap<String, Object>(){{
                        put("message", "password errata");
                        put("correct password", customer.getPassword());
                        put("search password", passwordEncoder.encode( credentials.getPassword() ));
                    }});
                }};
            }


            System.out.println("Customer");
            System.out.println( customer );


            /*
            Authentication authentication = authManager.authenticate( new UsernamePasswordAuthenticationToken( credentials.getEmail(), credentials.getPassword() ) );
            Customer customerToAuth = (Customer) authentication.getPrincipal();
            String accessToken = jwtUtil.generateAccessToken(customerToAuth);

            System.out.println("customerToAuth");
            System.out.println( customerToAuth );
            System.out.println("accessToken");
            System.out.println( accessToken );
            */




        }
        catch (Exception e) {

            // probabilmente email non esiste

            System.out.println("errore al login");
            e.printStackTrace();
            result = new HashMap<String, Object>(){{
                put("status", "fail");
                put("error", new HashMap<String, Object>(){{
                    put("message", "Eccezione");
                    put("exception", e.getMessage());
                }});
            }};

        }



        return
            ResponseEntity
                .status(HttpStatus.OK)
//                .body( customerService.getAllCustomer( page == null ? 0 : Math.toIntExact(page) ) )
                .build()
        ;


    }

}
